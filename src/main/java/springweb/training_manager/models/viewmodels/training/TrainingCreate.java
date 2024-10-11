package springweb.training_manager.models.viewmodels.training;

import lombok.Getter;
import springweb.training_manager.models.viewmodels.exercise.ExerciseTraining;

import java.util.List;

/**
 * View model that contains all objects required to create or edit `Training` entity.
 * Used in React frontend app.
 */
@Getter
public class TrainingCreate {
    private final TrainingRead training;
    private final List<ExerciseTraining> allExercises;

    public TrainingCreate(List<ExerciseTraining> allTrainings) {
        this(null, allTrainings);
    }

    public TrainingCreate(TrainingRead toEdit, List<ExerciseTraining> allExercises) {
        this.training = toEdit;
        this.allExercises = allExercises;
    }
}
