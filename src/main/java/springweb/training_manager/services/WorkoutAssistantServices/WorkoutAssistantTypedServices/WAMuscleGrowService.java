package springweb.training_manager.services.WorkoutAssistantServices.WorkoutAssistantTypedServices;

import org.springframework.stereotype.Service;
import springweb.training_manager.models.entities.*;
import springweb.training_manager.models.viewmodels.workout_assistant.BodyPartWorkoutStatistics;
import springweb.training_manager.models.viewmodels.workout_assistant.WorkoutAssistantWrite;
import springweb.training_manager.repositories.for_controllers.TrainingRepository;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static springweb.training_manager.models.viewmodels.workout_assistant.BodyPartWorkoutStatistics.MIN_ADVANCE_LEVEL;

@Service
public class WAMuscleGrowService extends WATypedBase {

    public WAMuscleGrowService(
        TrainingRepository trainingRepository
    ) {
        super(trainingRepository);
    }

    @Override
    public Map<BodyPart, List<Training>> getTrainingsByBodyParts(
        List<BodyPart> bodyParts,
        User loggedUser
    ) {
        return getTrainingsForBodyParts(
            bodyParts,
            3,
            loggedUser
        );
    }

    @Override
    float getLowerLimit(AcceptableRanges ranges) {
        return switch (ranges) {
            case ROUNDS -> 1;
            case REPETITIONS -> 6;
            case TIME -> 0.25f;
            case WEIGHT -> 0.7f;
        };
    }

    @Override
    float getUpperLimit(AcceptableRanges ranges) {
        return switch (ranges) {
            case ROUNDS -> 4;
            case REPETITIONS -> 10;
            case TIME -> getLowerLimit(AcceptableRanges.TIME) + 1f;
            case WEIGHT -> getLowerLimit(AcceptableRanges.WEIGHT) + 1f;
        };
    }

    protected boolean changeParameters(
        ExerciseParameters newParams,
        boolean increase,
        ExerciseParameters oldParams
    ) {
        var repetitionMode = oldParams.getRepetition() != 0;
        var amountOutOfBounds = repetitionMode
            ? changeRepetitionParameters(
            newParams,
            increase,
            oldParams.getRepetition()
        )
            : changeTimeParameters(
            newParams,
            increase,
            oldParams
        );

        if (!amountOutOfBounds)
            return false;

        var weightsOutOfBounds = changeWeightsParameters(
            newParams,
            increase,
            oldParams.getWeights()
        );

        if (!weightsOutOfBounds)
            return false;

        if (!repetitionMode) {
            var oldTime = oldParams.getTime();
            var timeRange = increase
                ? getUpperLimit(AcceptableRanges.TIME)
                : getLowerLimit(AcceptableRanges.TIME);
            var secondsToAdd = oldTime.toSecondOfDay() * timeRange;
            var newTime = LocalTime.ofSecondOfDay((long) secondsToAdd);
            newParams.setTime(newTime);
        } else {
            var repetitionRange = increase
                ? getUpperLimit(AcceptableRanges.REPETITIONS)
                : getLowerLimit(AcceptableRanges.REPETITIONS);
            newParams.setRepetition((int) repetitionRange);
        }

        var weightsRange = increase
            ? getUpperLimit(AcceptableRanges.TIME)
            : getLowerLimit(AcceptableRanges.TIME);
        newParams.setWeights((short) weightsRange);

        return true;
    }

    private ExerciseParameters prepExerciseParameters(
        TrainingExercise trainingExercise,
        int advanceLevel
    ) {
        var oldParams = trainingExercise.getParameters();
        var newParams = new ExerciseParameters(
            0,
            oldParams.getRounds(),
            oldParams.getRepetition(),
            oldParams.getWeights(),
            oldParams.getTime()
        );

        var increase = advanceLevel > 3;
        var multiplier = Math.abs(advanceLevel - 3) + 1;
        for (int i = 0; i < 8 * multiplier; i++)
            if (changeParameters(newParams, increase, oldParams))
                break;

        return newParams;
    }

    private TrainingExercise prepTrainingExercises(
        TrainingExercise trainingExercise,
        int advanceLevel
    ) {
        var newParameters = prepExerciseParameters(
            trainingExercise,
            advanceLevel
        );

        return new TrainingExercise(
            trainingExercise.getTraining(),
            trainingExercise.getExercise(),
            newParameters
        );
    }

    /**
     * This method prepares <code>BodyPartWorkoutStatistics</code> objects and checks if
     * any changes are required for training parameters.
     *
     * @param statistics data provided from user in form, necessary to decide parameters
     *                   for exercises in training
     *
     * @return False if no training need to be changed. In other words, if for every body
     * part advanceLevel is medium then for every training there should be used default
     * parameter and no changes are necessary.
     */
    private static boolean prepAndValidateWorkoutStatistics(
        Map<BodyPart, BodyPartWorkoutStatistics> statistics
    ) {
        var needsChanges = false;
        for (var entry : statistics.entrySet()) {
            BodyPartWorkoutStatistics bodyPartWorkoutStatistics = entry.getValue();
            var advanceLevel = bodyPartWorkoutStatistics.getAdvanceLevel();
            if (bodyPartWorkoutStatistics.getLastWorkout() > 2) {
                advanceLevel = Math.max(advanceLevel - 1, MIN_ADVANCE_LEVEL);
                bodyPartWorkoutStatistics.setAdvanceLevel(advanceLevel);
            }

            if (advanceLevel != 3)
                needsChanges = true;
        }

        return needsChanges;
    }

    @Override
    Training prepTrainingForGoal(
        WorkoutAssistantWrite data,
        Training toPrepare
    ) {
        var muscleGrowWrite = data.getMuscleGrow();
        var workoutStatistics = muscleGrowWrite.getBodyPartWorkoutStatistics();

        if (!prepAndValidateWorkoutStatistics(workoutStatistics))
            return toPrepare;

        var copiedTraining = new Training();
        copiedTraining.copy(toPrepare);
        var trainingExercises = copiedTraining.getTrainingExercises();

        List<TrainingExercise> preparedTrainingExercises = trainingExercises
            .stream()
            .map(
                trainingExercise -> {
                    var bodyPart = trainingExercise.getExercise()
                        .getBodyPart();
                    if (!workoutStatistics.containsKey(bodyPart))
                        return trainingExercise;

                    var statistics = workoutStatistics.get(bodyPart);
                    var advanceLevel = statistics.getAdvanceLevel();

                    if (advanceLevel == 3)
                        return trainingExercise;

                    return prepTrainingExercises(trainingExercise, advanceLevel);
                }
            )
            .toList();

        copiedTraining.setTrainingExercises(preparedTrainingExercises);
        return copiedTraining;
    }
}
