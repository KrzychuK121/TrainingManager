package springweb.training_manager.models.viewmodels.exercise;

import lombok.Getter;
import springweb.training_manager.models.entities.BodyPart;
import springweb.training_manager.models.entities.Difficulty;
import springweb.training_manager.models.entities.Exercise;
import springweb.training_manager.models.schemas.ExerciseSchema;
import springweb.training_manager.models.viewmodels.training.TrainingExercise;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ExerciseRead extends ExerciseSchema {
    private List<TrainingExercise> trainings = new ArrayList<>();
    private final String difficultyDesc;
    private final String bodyPartDesc;

    public ExerciseRead(Exercise exercise) {
        this.id = exercise.getId();
        this.name = exercise.getName();
        this.description = exercise.getDescription();
        this.rounds = exercise.getRounds();
        this.repetition = exercise.getRepetition();
        this.weights = exercise.getWeights();
        this.time = exercise.getTime();
        this.bodyPart = exercise.getBodyPart();
        this.bodyPartDesc = BodyPart.getBodyDesc(bodyPart);
        this.difficulty = exercise.getDifficulty();
        this.difficultyDesc = Difficulty.getEnumDesc(difficulty);
        this.trainings = TrainingExercise.toTrainingExerciseList(exercise.getTrainings());
    }

    public static List<ExerciseRead> toExerciseReadList(final List<Exercise> list) {
        List<ExerciseRead> result = new ArrayList<>(list.size());
        list.forEach(exercise -> result.add(new ExerciseRead(exercise)));
        return result;
    }

    public void setTrainings(List<TrainingExercise> trainings) {
        this.trainings = trainings;
    }
}
