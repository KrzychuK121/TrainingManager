package springweb.training_manager.models.viewmodels.training;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * This read model is used in view where user can follow his training planned to do.
 */
@AllArgsConstructor
@Getter
public class WorkoutTrainingRead {
    private int routineId;
    private TrainingRead trainingRead;
}
