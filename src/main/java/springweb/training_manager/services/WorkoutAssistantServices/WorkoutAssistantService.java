package springweb.training_manager.services.WorkoutAssistantServices;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import springweb.training_manager.models.entities.*;
import springweb.training_manager.models.viewmodels.exercise_parameters.ExerciseParametersRead;
import springweb.training_manager.models.viewmodels.training.TrainingRead;
import springweb.training_manager.models.viewmodels.validation.ValidationErrors;
import springweb.training_manager.models.viewmodels.workout_assistant.MuscleGrowWrite;
import springweb.training_manager.models.viewmodels.workout_assistant.WeightReductionWrite;
import springweb.training_manager.models.viewmodels.workout_assistant.WorkoutAssistantWrite;
import springweb.training_manager.repositories.for_controllers.TrainingRepository;
import springweb.training_manager.services.ExerciseParametersService;
import springweb.training_manager.services.TrainingPlanService;
import springweb.training_manager.services.TrainingService;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkoutAssistantService {
    private final TrainingRepository trainingRepository;
    private final TrainingPlanService trainingPlanService;

    public ValidationErrors validateAndPrepare(
        WorkoutAssistantWrite workoutAssistantWrite,
        BindingResult result
    ) {
        if (result.hasErrors())
            return ValidationErrors.createFrom(result);

        var earliest = workoutAssistantWrite.getEarliestTrainingStart();
        var latest = workoutAssistantWrite.getLatestTrainingStart();

        if (earliest.isAfter(latest)) {
            workoutAssistantWrite.setEarliestTrainingStart(latest);
            workoutAssistantWrite.setLatestTrainingStart(earliest);
        }

        return null;
    }

    private Map<BodyPart, List<Training>> getTrainingsForMuscleGrow(
        List<BodyPart> bodyParts,
        User loggedUser
    ) {
        var toReturn = new HashMap<BodyPart, List<Training>>();
        bodyParts.forEach(
            bodyPart -> {
                var trainings = trainingRepository.findForUseByMostBodyPart(
                    loggedUser.getId(),
                    bodyPart.name(),
                    1
                );

                toReturn.put(bodyPart, trainings);
            }
        );

        return toReturn;
    }

    private Map<BodyPart, List<Training>> getTrainingsForCardio(User loggedUser) {
        var trainings = trainingRepository.findForUseByMostBodyPart(
            loggedUser.getId(),
            BodyPart.CARDIO.name(),
            1
        );

        var toReturn = new HashMap<BodyPart, List<Training>>();
        toReturn.put(BodyPart.CARDIO, trainings);

        return toReturn;
    }

    private Map<BodyPart, List<Training>> prepTrainingForUserGoal(
        MuscleGrowWrite muscleGrowWrite,
        Map<BodyPart, List<Training>> bodyPartsTrainings
    ) {
        return bodyPartsTrainings;
    }

    private static boolean kcalDiffAcceptable(
        int kcalDifference,
        int acceptableDifference
    ) {
        return Math.abs(kcalDifference) < acceptableDifference;
    }

    private static boolean kcalDiffAcceptableForWeightRedu(
        int kcalDifference
    ) {
        return kcalDiffAcceptable(kcalDifference, 50);
    }

    private static LocalTime calcNewTrainingTime(
        int newAmountCount,
        ExerciseParameters newParams,
        ExerciseParameters oldParams
    ) {
        var increase = newAmountCount > 0;
        var newTime = newParams.getTime();
        if (increase)
            newTime = newTime.plusMinutes(newAmountCount);
        else
            newTime = newTime.minusMinutes(newAmountCount);

        var oldTimeMinutes = oldParams.getTime()
            .toSecondOfDay() / 60;
        int newTimeMinutes = newTime.toSecondOfDay() / 60;
        if (Math.abs(oldTimeMinutes / newTimeMinutes) < 0.5)
            newTime = LocalTime.of(0, 0)
                .plusMinutes((int) (oldTimeMinutes * 0.5));
        else if (Math.abs(oldTimeMinutes / newTimeMinutes) > 1.5)
            newTime = LocalTime.of(0, 0)
                .plusMinutes((int) (oldTimeMinutes * 1.5));
        return newTime;
    }

    private ExerciseParameters calcParamsToTargetKcalForWeightRedu(
        TrainingExercise trainingExercise,
        int kcalDifference,
        int acceptableKcalDifference
    ) {
        // rounds range 1-4
        // reps range 6-30
        // time 50% range
        // weight 25% range
        var multiplier = kcalDifference < 0
            ? -1
            : 1;
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

        var roundBurnedKcal = ExerciseParametersService.calcBurnedKcalPerRound(
            defaultBurnedKcal,
            new ExerciseParametersRead(oldParams)
        );

        if (Math.abs(kcalDifference) > roundBurnedKcal) {
            int newRoundsCount = kcalDifference / roundBurnedKcal;
            var newRounds = newParams.getRounds() + newRoundsCount;

            if (newRounds < 1)
                newRounds = 1;
            else if (newRounds > 5)
                newRounds = 5;

            kcalDifference += -multiplier * roundBurnedKcal * newRoundsCount;

            newParams.setRounds(newRounds);
        }

        if (kcalDiffAcceptable(kcalDifference, acceptableKcalDifference))
            return newParams;

        int newAmountCount = kcalDifference / defaultBurnedKcal;
        if (newParams.getRepetition() != 0) {
            var newRepetition = newParams.getRepetition() + newAmountCount;
            if (newRepetition < 6)
                newRepetition = 6;
            else if (newRepetition > 30)
                newRepetition = 30;

            kcalDifference += (kcalDifference < 0 ? 1 : -1) * defaultBurnedKcal * newRepetition;

            newParams.setRepetition(newRepetition);
        } else {
            var newTime = calcNewTrainingTime(
                newAmountCount,
                newParams,
                oldParams
            );

            newParams.setTime(newTime);
        }

        if (kcalDiffAcceptable(kcalDifference, acceptableKcalDifference))
            return newParams;

        // TODO: Implement logic with weights

        return newParams;
    }

    private Training prepTrainingForUserGoal(
        WeightReductionWrite weightReductionWrite,
        Training toPrepare
    ) {
        var dailyKcalReduction = weightReductionWrite.getDailyKcalReduction();
        var totalKcalBurn = TrainingService.getTotalBurnedKcal(new TrainingRead(toPrepare));
        var kcalDifference = dailyKcalReduction - totalKcalBurn;
        if (kcalDiffAcceptableForWeightRedu(kcalDifference))
            return toPrepare;

        var trainingExercises = toPrepare.getTrainingExercises();
        ExecutorService executor = Executors.newFixedThreadPool(trainingExercises.size());
        var prepThreads = new ArrayList<Callable<ExerciseParameters>>();

        var kcalDifferenceByExerciseAmount = kcalDifference / trainingExercises.size();
        for (var trainingExercise : trainingExercises) {
            prepThreads.add(
                () -> calcParamsToTargetKcalForWeightRedu(
                    trainingExercise,
                    kcalDifferenceByExerciseAmount,
                    (int) (Math.abs(kcalDifferenceByExerciseAmount) * 0.1)
                )
            );
        }

        try {
            List<Future<ExerciseParameters>> futurePrepExercises = executor.invokeAll(prepThreads);
            List<ExerciseParameters> prepExerciseList = futurePrepExercises.stream()
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
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            executor.shutdown();
        }

        return toPrepare;
    }

    private Map<BodyPart, List<Training>> prepTrainingForUserGoal(
        WeightReductionWrite weightReductionWrite,
        Map<BodyPart, List<Training>> bodyPartsTrainings
    ) {
        bodyPartsTrainings.replaceAll(
            (bodyPart, trainings) -> trainings.stream()
                .map(training -> prepTrainingForUserGoal(weightReductionWrite, training))
                .toList()
        );

        return bodyPartsTrainings;
    }

    /**
     * This method returns next day of the week based on index. The idea is to generate
     * days with as many gap days for rest as possible. E.g. if loop will be executed 4
     * times, the function will return values: SUNDAY, FRIDAY, WEDNESDAY, MONDAY.
     * <p>
     * If loop iterates more than 4 times, function will generate days in the same way but
     * starting from SATURDAY
     *
     * @param index value from 0 to 7 (one day for training or whole week).
     *
     * @return Weekdays value that has 1 day gap from previous day.
     */
    private static Weekdays getNextWeekday(int index) {
        var weekdays = Weekdays.values();
        var weekdaysLastIndex = weekdays.length - 1;
        var decrease = index < 4
            ? 0
            : 7;
        var nextWeekdaysIndex = weekdaysLastIndex - (2 * index - decrease);
        return weekdays[nextWeekdaysIndex];
    }

    private List<TrainingSchedule> planSchedules(
        WorkoutAssistantWrite workoutAssistantWrite,
        Map<BodyPart, List<Training>> bodyPartsTrainings
    ) {
        var schedules = new ArrayList<TrainingSchedule>(
            workoutAssistantWrite.getWorkoutDays()
        );

        var workoutDays = workoutAssistantWrite.getWorkoutDays();
        for (int i = 0; i < workoutDays; i++) {
            var nextBodyPart = bodyPartsTrainings.keySet()
                .stream()
                .toList()
                .get(i % bodyPartsTrainings.size());
            var trainings = bodyPartsTrainings.get(nextBodyPart);

            var nextTrainingIndex = i % trainings.size();
            var nextTraining = trainings.get(nextTrainingIndex);
            var nextDay = getNextWeekday(i);

            var newSchedule = new TrainingSchedule(
                nextTraining.getId(),
                nextDay
            );

            schedules.add(
                trainingPlanService.prepTrainingSchedule(
                    newSchedule
                )
            );
        }

        return schedules;
    }

    public void planTrainingRoutine(
        WorkoutAssistantWrite workoutAssistantWrite,
        User loggedUser
    ) {
        var workoutType = workoutAssistantWrite.getMuscleGrow() != null
            ? WorkoutType.MUSCLE_GROW
            : WorkoutType.WEIGHT_REDUCTION;

        Map<BodyPart, List<Training>> trainings = workoutType == WorkoutType.MUSCLE_GROW
            ? getTrainingsForMuscleGrow(List.of(), loggedUser)
            : getTrainingsForCardio(loggedUser);

        trainings = workoutType == WorkoutType.MUSCLE_GROW
            ? prepTrainingForUserGoal(
            workoutAssistantWrite.getMuscleGrow(),
            trainings
        )
            : prepTrainingForUserGoal(
            workoutAssistantWrite.getWeightReduction(),
            trainings
        );

        /*var schedules = planSchedules(
            workoutAssistantWrite,
            trainings
        );*/
    }
}

enum WorkoutType {
    MUSCLE_GROW,
    WEIGHT_REDUCTION
}