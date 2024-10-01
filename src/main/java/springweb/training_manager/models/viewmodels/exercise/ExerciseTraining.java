package springweb.training_manager.models.viewmodels.exercise;

import lombok.NoArgsConstructor;
import springweb.training_manager.models.entities.BodyPart;
import springweb.training_manager.models.entities.Difficulty;
import springweb.training_manager.models.entities.Exercise;
import springweb.training_manager.models.schemas.ExerciseSchema;
import springweb.training_manager.models.viewmodels.Castable;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class ExerciseTraining extends ExerciseSchema implements Castable<Exercise> {

    public ExerciseTraining(Exercise exercise) {
        this.id = exercise.getId();
        this.name = exercise.getName();
        this.description = exercise.getDescription();
        this.rounds = exercise.getRounds();
        this.repetition = exercise.getRepetition();
        this.weights = exercise.getWeights();
        this.time = exercise.getTime();
        this.bodyPart = exercise.getBodyPart();
        this.difficulty = exercise.getDifficulty();
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

    void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setRounds(int rounds) {
        this.rounds = rounds;
    }

    public void setRepetition(int repetition) {
        this.repetition = repetition;
    }

    public void setWeights(short weights) {
        this.weights = weights;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public void setBodyPart(BodyPart bodyPart) {
        this.bodyPart = bodyPart;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    public Exercise toEntity() {
        var toReturn = new Exercise(
            name,
            description,
            rounds,
            repetition,
            weights,
            time,
            bodyPart,
            difficulty
        );

        toReturn.setId(id);

        return toReturn;
    }

}
