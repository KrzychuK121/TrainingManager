package springweb.training_manager.services.WorkoutAssistantServices.WorkoutAssistantTypedServices;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import springweb.training_manager.models.entities.BodyPart;
import springweb.training_manager.models.entities.ExerciseParameters;
import springweb.training_manager.models.entities.Training;
import springweb.training_manager.models.entities.User;
import springweb.training_manager.models.viewmodels.workout_assistant.WorkoutAssistantWrite;
import springweb.training_manager.repositories.for_controllers.TrainingRepository;

import java.time.LocalTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public abstract class WATypedBase {
    private final TrainingRepository trainingRepository;

    protected Map<BodyPart, List<Training>> getTrainingsForBodyParts(
        List<BodyPart> bodyParts,
        int bodyPartCount,
        User loggedUser
    ) {
        if (bodyParts == null || bodyParts.isEmpty())
            return Collections.emptyMap();
        var toReturn = new HashMap<BodyPart, List<Training>>();
        bodyParts.forEach(
            bodyPart -> {
                var trainings = trainingRepository.findForUseByMostBodyPart(
                    loggedUser.getId(),
                    bodyPart.name(),
                    bodyPartCount
                );

                toReturn.put(bodyPart, trainings);
            }
        );

        return toReturn;
    }

    abstract float getLowerLimit(AcceptableRanges ranges);

    abstract float getUpperLimit(AcceptableRanges ranges);

    protected boolean changeRoundsParameters(
        ExerciseParameters newParams,
        boolean increase,
        int defaultValue
    ) {
        var outOfBounds = false;
        var changingValue = increase ? 1 : -1;
        var newRounds = newParams.getRounds() + changingValue;
        if (
            newRounds < getLowerLimit(AcceptableRanges.ROUNDS)
                || newRounds > getUpperLimit(AcceptableRanges.ROUNDS)
        ) {
            outOfBounds = true;
            newRounds = defaultValue;
        }

        newParams.setRounds(newRounds);
        return outOfBounds;
    }

    protected boolean changeRoundsParameters(
        ExerciseParameters newParams,
        boolean increase
    ) {
        var defaultValue = increase
            ? getUpperLimit(AcceptableRanges.ROUNDS)
            : getLowerLimit(AcceptableRanges.ROUNDS);

        return changeRoundsParameters(
            newParams,
            increase,
            (int) defaultValue
        );
    }

    protected boolean changeTimeParameters(
        ExerciseParameters newParams,
        boolean increase,
        ExerciseParameters oldParams,
        LocalTime defaultValue
    ) {
        var outOfBounds = false;
        var newParamsOldTime = newParams.getTime();
        var oldTime = oldParams.getTime();
        LocalTime newTime;
        if (newParamsOldTime.getHour() == 0 && newParamsOldTime.getMinute() < 5)
            newTime = increase
                ? newParamsOldTime.plusSeconds(30)
                : newParamsOldTime.minusSeconds(30);
        else
            newTime = increase
                ? newParamsOldTime.plusMinutes(1)
                : newParamsOldTime.minusMinutes(1);

        if (
            newTime.toSecondOfDay() < (
                oldTime.toSecondOfDay() * getLowerLimit(AcceptableRanges.TIME)
            )
                || newTime.toSecondOfDay() > (
                oldTime.toSecondOfDay() * getUpperLimit(AcceptableRanges.TIME)
            )
        ) {
            newTime = defaultValue;
            outOfBounds = true;
        }

        newParams.setTime(newTime);
        return outOfBounds;
    }

    protected boolean changeTimeParameters(
        ExerciseParameters newParams,
        boolean increase,
        ExerciseParameters oldParams
    ) {
        var multiplier = increase
            ? getUpperLimit(AcceptableRanges.TIME)
            : getLowerLimit(AcceptableRanges.TIME);
        var defaultSeconds = oldParams.getTime()
            .toSecondOfDay() * multiplier;
        var defaultValue = LocalTime.ofSecondOfDay((long) defaultSeconds);

        return changeTimeParameters(
            newParams,
            increase,
            oldParams,
            defaultValue
        );
    }

    protected boolean changeRepetitionParameters(
        ExerciseParameters newParams,
        boolean increase,
        int defaultValue
    ) {
        var outOfBounds = false;
        var newRepetition = newParams.getRepetition() + (increase ? 1 : -1);
        if (
            newRepetition < getLowerLimit(AcceptableRanges.REPETITIONS)
                || newRepetition > getUpperLimit(AcceptableRanges.REPETITIONS)
        ) {
            outOfBounds = true;
            newRepetition = defaultValue;
        }

        newParams.setRepetition(newRepetition);
        return outOfBounds;
    }

    protected boolean changeRepetitionParameters(
        ExerciseParameters newParams,
        boolean increase
    ) {
        var defaultValue = increase
            ? getUpperLimit(AcceptableRanges.REPETITIONS)
            : getLowerLimit(AcceptableRanges.REPETITIONS);
        return changeRepetitionParameters(
            newParams,
            increase,
            (int) defaultValue
        );
    }

    protected boolean changeWeightsParameters(
        ExerciseParameters newParams,
        boolean increase,
        short defaultValue
    ) {
        var outOfBounds = false;
        var newWeights = newParams.getWeights() + (increase ? 1 : -1);
        if (
            newWeights < getLowerLimit(AcceptableRanges.WEIGHT)
                || newWeights > getUpperLimit(AcceptableRanges.WEIGHT)
        ) {
            outOfBounds = true;
            newWeights = defaultValue;
        }

        newParams.setWeights((short) newWeights);
        return outOfBounds;
    }

    protected boolean changeWeightsParameters(
        ExerciseParameters newParams,
        boolean increase
    ) {
        var defaultValue = increase
            ? getUpperLimit(AcceptableRanges.WEIGHT)
            : getLowerLimit(AcceptableRanges.WEIGHT);
        return changeRepetitionParameters(
            newParams,
            increase,
            (short) defaultValue
        );
    }

    public Map<BodyPart, List<Training>> prepTrainingsForUserGoal(
        WorkoutAssistantWrite workoutAssistantWrite,
        Map<BodyPart, List<Training>> bodyPartsTrainings
    ) {
        bodyPartsTrainings.replaceAll(
            (bodyPart, trainings) -> trainings.stream()
                .map(training -> prepTrainingForGoal(workoutAssistantWrite, training))
                .toList()
        );

        return bodyPartsTrainings;
    }

    public abstract Map<BodyPart, List<Training>> getTrainingsByBodyParts(
        List<BodyPart> bodyParts,
        User loggedUser
    );

    abstract Training prepTrainingForGoal(
        WorkoutAssistantWrite data,
        Training toPrepare
    );
}

enum AcceptableRanges {
    ROUNDS,
    REPETITIONS,
    TIME,
    WEIGHT;
}
