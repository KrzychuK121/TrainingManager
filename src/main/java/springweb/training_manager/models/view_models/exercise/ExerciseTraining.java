package springweb.training_manager.models.view_models.exercise;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import springweb.training_manager.models.entities.BodyPart;
import springweb.training_manager.models.entities.Exercise;
import springweb.training_manager.models.entities.ExerciseParameters;
import springweb.training_manager.models.schemas.ExerciseSchema;
import springweb.training_manager.models.view_models.Castable;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class ExerciseTraining extends ExerciseSchema implements Castable<Exercise> {
    private final String bodyPartDesc;
    private final int parametersId;
    private final int rounds;
    private final int repetition;
    private final short weights;
    private final LocalTime time;
    private final boolean exercisePrivate;

    public ExerciseTraining(Exercise exercise) {
        super(
            exercise.getId(),
            exercise.getName(),
            exercise.getDescription(),
            exercise.getBodyPart(),
            exercise.getDefaultBurnedKcal()
        );
        this.parametersId = exercise.getParameters()
            .getId();
        this.rounds = exercise.getParameters()
            .getRounds();
        this.repetition = exercise.getParameters()
            .getRepetition();
        this.weights = exercise.getParameters()
            .getWeights();
        this.time = exercise.getParameters()
            .getTime();
        this.exercisePrivate = exercise.getOwner() != null;
        this.bodyPartDesc = BodyPart.getBodyDesc(bodyPart);
    }

    public ExerciseTraining(
        Exercise exercise,
        ExerciseParameters parameters
    ) {
        super(
            exercise.getId(),
            exercise.getName(),
            exercise.getDescription(),
            exercise.getBodyPart(),
            exercise.getDefaultBurnedKcal()
        );
        this.parametersId = parameters.getId();
        this.rounds = parameters.getRounds();
        this.repetition = parameters.getRepetition();
        this.weights = parameters.getWeights();
        this.time = parameters.getTime();
        this.bodyPartDesc = BodyPart.getBodyDesc(bodyPart);
        this.exercisePrivate = exercise.getOwner() != null;
    }

    public static List<ExerciseTraining> toExerciseTrainingList(final List<Exercise> list) {
        if (list == null)
            return null;
        List<ExerciseTraining> result = new ArrayList<>(list.size());
        list.forEach(exercise -> result.add(new ExerciseTraining(exercise)));
        return result;
    }

    public static List<Exercise> toExerciseList(final List<ExerciseTraining> list) {
        List<Exercise> result = new ArrayList<>(list.size());
        list.forEach(exerciseTraining ->
            result.add(exerciseTraining.toEntity())
        );

        return result;
    }

    public ExerciseParameters getExerciseParameters() {
        return new ExerciseParameters(
            parametersId,
            rounds,
            repetition,
            weights,
            time
        );
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setBodyPart(BodyPart bodyPart) {
        this.bodyPart = bodyPart;
    }

    @Override
    public Exercise toEntity() {
        var toReturn = new Exercise(
            name,
            description,
            bodyPart,
            getExerciseParameters(),
            defaultBurnedKcal
        );
        toReturn.setId(id);

        return toReturn;
    }

}
