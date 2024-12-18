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
import springweb.training_manager.services.WorkoutAssistantServices.AcceptableRanges;

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

    private static boolean kcalDiffAcceptable(
        int kcalDifference,
        int acceptableDifference
    ) {
        return Math.abs(kcalDifference) < acceptableDifference;
    }

    private static boolean changeParametersByTime(
        ExerciseParameters newParams,
        boolean increase,
        ExerciseParameters oldParams
    ) {
        var oldTime = newParams.getTime();

        LocalTime newTime;
        if (oldTime.getHour() == 0 && oldTime.getMinute() < 5)
            newTime = increase
                ? oldTime.plusSeconds(30)
                : oldTime.minusSeconds(30);
        else
            newTime = increase
                ? oldTime.plusMinutes(1)
                : oldTime.minusMinutes(1);

        if (
            newTime.toSecondOfDay() < (
                oldTime.toSecondOfDay() * AcceptableRanges.TIME.LOWER_LIMIT
            )
        ) {
            newTime = oldParams.getTime();
            newParams.setRounds(newParams.getRounds() - 1);
        } else if (
            newTime.toSecondOfDay() > (
                oldTime.toSecondOfDay() * AcceptableRanges.TIME.UPPER_LIMIT
            )
        ) {
            newTime = oldParams.getTime();
            newParams.setRounds(newParams.getRounds() + 1);
        }

        newParams.setTime(newTime);
        if (
            newParams.getRounds() == AcceptableRanges.ROUNDS.UPPER_LIMIT
                && newTime.toSecondOfDay() == (
                oldTime.toSecondOfDay() * AcceptableRanges.TIME.UPPER_LIMIT
            )
        )
            return true;
        return newParams.getRounds() == AcceptableRanges.ROUNDS.LOWER_LIMIT
            && newTime.toSecondOfDay() == (
            oldTime.toSecondOfDay() * AcceptableRanges.TIME.LOWER_LIMIT
        );
    }

    private static boolean changeParametersByRepetition(
        ExerciseParameters newParams,
        boolean increase,
        ExerciseParameters oldParams
    ) {
        var newRepetition = newParams.getRepetition() + (increase ? 1 : -1);
        if (newRepetition < 6) {
            newRepetition = oldParams.getRepetition();
            newParams.setRounds(newParams.getRounds() - 1);
        } else if (newRepetition > 30) {
            newRepetition = oldParams.getRepetition();
            newParams.setRounds(newParams.getRounds() + 1);
        }

        newParams.setRepetition(newRepetition);

        if (
            newParams.getRounds() == AcceptableRanges.ROUNDS.UPPER_LIMIT
                && newRepetition == AcceptableRanges.REPETITIONS.UPPER_LIMIT
        )
            return true;
        return newParams.getRounds() == AcceptableRanges.ROUNDS.LOWER_LIMIT
            && newRepetition == AcceptableRanges.REPETITIONS.LOWER_LIMIT;
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
        if (kcalDiffAcceptable(kcalDifference, 50))
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
