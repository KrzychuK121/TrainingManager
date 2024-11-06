package springweb.training_manager.models.viewmodels.exercise;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import springweb.training_manager.models.entities.BodyPart;
import springweb.training_manager.models.entities.Difficulty;
import springweb.training_manager.models.entities.Exercise;
import springweb.training_manager.models.entities.ExerciseParameters;
import springweb.training_manager.models.schemas.ExerciseSchema;
import springweb.training_manager.models.viewmodels.Castable;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class ExerciseTraining extends ExerciseSchema implements Castable<Exercise> {
    private final String difficultyDesc;
    private final String bodyPartDesc;
    private final int parameters_id;
    private final int rounds;
    private final int repetition;
    private final short weights;
    private final LocalTime time;
    private final Difficulty difficulty;

    public ExerciseTraining(Exercise exercise) {
        super(
            exercise.getId(),
            exercise.getName(),
            exercise.getDescription(),
            exercise.getBodyPart()
        );
        this.parameters_id = exercise.getParameters()
            .getId();
        this.rounds = exercise.getParameters()
            .getRounds();
        this.repetition = exercise.getParameters()
            .getRepetition();
        this.weights = exercise.getParameters()
            .getWeights();
        this.time = exercise.getParameters()
            .getTime();
        this.difficulty = exercise.getParameters()
            .getDifficulty();
        this.bodyPartDesc = BodyPart.getBodyDesc(bodyPart);
        this.difficultyDesc = Difficulty.getEnumDesc(difficulty);
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
            new ExerciseParameters(
                parameters_id,
                rounds,
                repetition,
                weights,
                time,
                difficulty
            )
        );
        toReturn.setId(id);

        return toReturn;
    }

}
