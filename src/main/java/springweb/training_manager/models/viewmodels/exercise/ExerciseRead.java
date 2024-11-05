package springweb.training_manager.models.viewmodels.exercise;

import lombok.Getter;
import springweb.training_manager.models.entities.BodyPart;
import springweb.training_manager.models.entities.Difficulty;
import springweb.training_manager.models.entities.Exercise;
import springweb.training_manager.models.schemas.ExerciseSchema;
import springweb.training_manager.models.viewmodels.training.TrainingExerciseVM;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ExerciseRead extends ExerciseSchema {
    private List<TrainingExerciseVM> trainings = new ArrayList<>();
    private final String difficultyDesc;
    private final String bodyPartDesc;

    public ExerciseRead(Exercise exercise) {
        super(
            exercise.getId(),
            exercise.getName(),
            exercise.getDescription(),
            exercise.getRounds(),
            exercise.getRepetition(),
            exercise.getWeights(),
            exercise.getTime(),
            exercise.getBodyPart(),
            exercise.getDifficulty()
        );
        this.bodyPartDesc = BodyPart.getBodyDesc(bodyPart);
        this.difficultyDesc = Difficulty.getEnumDesc(difficulty);
        this.trainings = TrainingExerciseVM.toTrainingExerciseList(exercise.getTrainings());
    }
}
