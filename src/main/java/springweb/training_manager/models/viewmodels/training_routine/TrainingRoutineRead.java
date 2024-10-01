package springweb.training_manager.models.viewmodels.training_routine;

import lombok.Getter;
import springweb.training_manager.models.entities.TrainingRoutine;
import springweb.training_manager.models.schemas.TrainingRoutineSchema;
import springweb.training_manager.models.viewmodels.user.UserRead;

@Getter
public class TrainingRoutineRead extends TrainingRoutineSchema {
    private final UserRead owner;

    public TrainingRoutineRead(TrainingRoutine trainingRoutine) {
        this.id = trainingRoutine.getId();
        this.active = trainingRoutine.isActive();
        this.owner = new UserRead(trainingRoutine.getOwner());
    }

}
