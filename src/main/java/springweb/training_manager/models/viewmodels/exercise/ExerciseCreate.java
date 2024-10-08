package springweb.training_manager.models.viewmodels.exercise;

import lombok.Getter;
import springweb.training_manager.models.viewmodels.enums.BodyPartsRead;
import springweb.training_manager.models.viewmodels.enums.DifficultiesRead;
import springweb.training_manager.models.viewmodels.training.TrainingExercise;

import java.util.List;

/**
 * View model that contains all objects required to create or edit `Exercise` entity with enums etc.
 * Used in React frontend app.
 */
@Getter
public class ExerciseCreate {
    private final ExerciseRead exercise;
    private final DifficultiesRead difficulties = new DifficultiesRead();
    private final BodyPartsRead bodyParts = new BodyPartsRead();
    private final List<TrainingExercise> allTrainings;

    public ExerciseCreate(List<TrainingExercise> allTrainings) {
        this(null, allTrainings);
    }

    public ExerciseCreate(ExerciseRead toEdit, List<TrainingExercise> allTrainings) {
        this.exercise = toEdit;
        this.allTrainings = allTrainings;
    }
}
