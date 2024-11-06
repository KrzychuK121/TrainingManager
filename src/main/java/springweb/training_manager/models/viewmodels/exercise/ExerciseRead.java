package springweb.training_manager.models.viewmodels.exercise;

import lombok.Getter;
import springweb.training_manager.models.entities.BodyPart;
import springweb.training_manager.models.entities.Difficulty;
import springweb.training_manager.models.entities.Exercise;
import springweb.training_manager.models.schemas.ExerciseSchema;
import springweb.training_manager.models.viewmodels.training.TrainingExerciseVM;

import java.time.LocalTime;
import java.util.List;

@Getter
public class ExerciseRead extends ExerciseSchema {
    private List<TrainingExerciseVM> trainings;
    private final String difficultyDesc;
    private final String bodyPartDesc;
    private final int parameters_id;
    private final int rounds;
    private final int repetition;
    private final short weights;
    private final LocalTime time;
    private final Difficulty difficulty;

    public ExerciseRead(Exercise exercise) {
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
        this.trainings = TrainingExerciseVM.toTrainingExerciseList(exercise.getTrainings());
    }
}
