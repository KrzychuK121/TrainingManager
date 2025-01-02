package springweb.training_manager.services.WorkoutAssistantServices.WorkoutAssistantTypedServices;

import org.springframework.stereotype.Service;
import springweb.training_manager.models.entities.*;
import springweb.training_manager.models.viewmodels.exercise_parameters.ExerciseParametersRead;
import springweb.training_manager.models.viewmodels.training.TrainingRead;
import springweb.training_manager.models.viewmodels.workout_assistant.WorkoutAssistantWrite;
import springweb.training_manager.repositories.for_controllers.ExerciseParametersRepository;
import springweb.training_manager.repositories.for_controllers.TrainingRepository;
import springweb.training_manager.services.ExerciseParametersService;
import springweb.training_manager.services.TrainingService;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Service
public class WAWeightReductionService extends WATypedBase {
    private final ExerciseParametersRepository parametersRepository;

    public WAWeightReductionService(
        TrainingRepository trainingRepository,
        ExerciseParametersRepository parametersRepository
    ) {
        super(trainingRepository);
        this.parametersRepository = parametersRepository;
    }

    @Override
    public Map<BodyPart, List<Training>> getTrainingsByBodyParts(
        List<BodyPart> bodyParts,
        User loggedUser
    ) {
        return getTrainingsForBodyParts(
            List.of(BodyPart.CARDIO),
            1,
            loggedUser
        );
    }

    @Override
    float getLowerLimit(AcceptableRanges ranges) {
        return switch (ranges) {
            case ROUNDS -> 1;
            case REPETITIONS -> 6;
            case TIME -> 0.5f;
            case WEIGHT -> 0.25f;
        };
    }

    @Override
    float getUpperLimit(AcceptableRanges ranges) {
        return switch (ranges) {
            case ROUNDS -> 4;
            case REPETITIONS -> 30;
            case TIME -> getLowerLimit(AcceptableRanges.TIME) + 1f;
            case WEIGHT -> getLowerLimit(AcceptableRanges.WEIGHT) + 1f;
        };
    }

    protected boolean changeParametersByTime(
        ExerciseParameters newParams,
        boolean increase,
        ExerciseParameters oldParams
    ) {
        var oldTime = oldParams.getTime();
        var timeOutOfBounds = changeTimeParameters(
            newParams,
            increase,
            oldParams,
            oldTime
        );
        if (!timeOutOfBounds)
            return false;

        var roundsOutOfBounds = changeRoundsParameters(
            newParams,
            increase
        );

        if (!roundsOutOfBounds)
            return false;

        var limit = increase
            ? getUpperLimit(AcceptableRanges.TIME)
            : getLowerLimit(AcceptableRanges.TIME);

        var newTimeSeconds = oldTime.toSecondOfDay() * limit;
        newParams.setTime(LocalTime.ofSecondOfDay((long) newTimeSeconds));
        return true;
    }

    protected boolean changeParametersByRepetition(
        ExerciseParameters newParams,
        boolean increase,
        ExerciseParameters oldParams
    ) {
        var oldRepetitions = oldParams.getRepetition();
        var repetitionOutOfBounds = changeRepetitionParameters(
            newParams,
            increase,
            oldRepetitions
        );
        if (!repetitionOutOfBounds)
            return false;

        var roundsOutOfBounds = changeRoundsParameters(
            newParams,
            increase
        );

        if (!roundsOutOfBounds)
            return false;

        var boundRepetitions = increase
            ? getUpperLimit(AcceptableRanges.REPETITIONS)
            : getLowerLimit(AcceptableRanges.REPETITIONS);
        newParams.setRepetition((int) boundRepetitions);
        return true;
    }

    private ExerciseParameters calcParamsForWeightRedu(
        TrainingExercise trainingExercise,
        int kcalDifference,
        int acceptableKcalDifference
    ) {
        var oldParams = trainingExercise.getParameters();
        var defaultBurnedKcal = trainingExercise.getExercise()
            .getDefaultBurnedKcal();
        var newParams = new ExerciseParameters(
            0,
            oldParams.getRounds(),
            oldParams.getRepetition(),
            oldParams.getWeights(),
            oldParams.getTime()
        );

        var targetCalories = ExerciseParametersService.calcTotalBurnedKcal(
            defaultBurnedKcal,
            new ExerciseParametersRead(oldParams)
        ) + kcalDifference;

        var currentBurnedKcal = ExerciseParametersService.calcTotalBurnedKcal(
            defaultBurnedKcal,
            new ExerciseParametersRead(newParams)
        );
        var difference = targetCalories - currentBurnedKcal;
        var oldDifference = difference;

        var repetitionExercise = newParams.getRepetition() != 0;

        while (Math.abs(difference) > acceptableKcalDifference) {
            // Checking if we can't match parameters to be in acceptable range.
            if (
                oldDifference * difference < 0
                    && Math.abs(difference) > acceptableKcalDifference
            )
                break;

            var increase = difference > 0;
            if (repetitionExercise) {
                if (changeParametersByRepetition(newParams, increase, oldParams)) break;
            } else {
                if (changeParametersByTime(newParams, increase, oldParams)) break;
            }

            oldDifference = difference;
            currentBurnedKcal = ExerciseParametersService.calcTotalBurnedKcal(
                defaultBurnedKcal,
                new ExerciseParametersRead(newParams)
            );
            difference = targetCalories - currentBurnedKcal;
        }

        return parametersRepository.findDuplication(newParams)
            .orElse(newParams);
    }

    private TrainingExercise prepTrainingForWeightRedu(
        TrainingExercise trainingExercise,
        int kcalDifference,
        int acceptableKcalDifference
    ) {
        var foundOrNewParams = calcParamsForWeightRedu(
            trainingExercise,
            kcalDifference,
            acceptableKcalDifference
        );

        return new TrainingExercise(
            trainingExercise.getTraining(),
            trainingExercise.getExercise(),
            foundOrNewParams
        );
    }

    private ArrayList<Callable<TrainingExercise>> getCalcParamsThreadsForWeightRedu(
        int kcalDifference,
        List<TrainingExercise> trainingExercises
    ) {
        var prepThreads = new ArrayList<Callable<TrainingExercise>>();

        var kcalDifferenceByExerciseAmount = kcalDifference / trainingExercises.size();
        for (var trainingExercise : trainingExercises) {
            prepThreads.add(
                () -> prepTrainingForWeightRedu(
                    trainingExercise,
                    kcalDifferenceByExerciseAmount,
                    (int) (Math.abs(kcalDifferenceByExerciseAmount) * 0.5)
                )
            );
        }
        return prepThreads;
    }

    @Override
    Training prepTrainingForGoal(
        WorkoutAssistantWrite data,
        Training toPrepare
    ) {
        var weightReductionWrite = data.getWeightReduction();
        var dailyKcalReduction = weightReductionWrite.getDailyKcalReduction();
        var totalKcalBurn = TrainingService.getTotalBurnedKcal(new TrainingRead(toPrepare));
        var kcalDifference = dailyKcalReduction - totalKcalBurn;
        if (Math.abs(kcalDifference) < 50)
            return toPrepare;

        var copiedTraining = new Training();
        copiedTraining.copy(toPrepare);

        var trainingExercises = copiedTraining.getTrainingExercises();
        ExecutorService executor = Executors.newFixedThreadPool(trainingExercises.size());
        var prepThreads = getCalcParamsThreadsForWeightRedu(kcalDifference, trainingExercises);

        try {
            List<Future<TrainingExercise>> futurePrepExercises = executor.invokeAll(prepThreads);
            List<TrainingExercise> prepTrainingExercises = futurePrepExercises.stream()
                .map(
                    future -> {
                        try {
                            return future.get();
                        } catch (InterruptedException | ExecutionException e) {
                            throw new RuntimeException(e);
                        }
                    }
                )
                .toList();

            copiedTraining.setTrainingExercises(prepTrainingExercises);
            return copiedTraining;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            executor.shutdown();
        }
    }
}
