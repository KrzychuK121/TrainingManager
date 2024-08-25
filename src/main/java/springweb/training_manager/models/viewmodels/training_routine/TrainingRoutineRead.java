package springweb.training_manager.models.viewmodels.training_routine;

import springweb.training_manager.models.entities.TrainingRoutine;
import springweb.training_manager.models.schemas.TrainingRoutineSchema;
import springweb.training_manager.models.viewmodels.user.UserRead;

public class TrainingRoutineRead extends TrainingRoutineSchema {
    private final UserRead owner;

    public TrainingRoutineRead(TrainingRoutine trainingRoutine) {
        this.id = trainingRoutine.getId();
        this.active = trainingRoutine.isActive();
        this.owner = new UserRead(trainingRoutine.getOwner());
    }

    public UserRead getOwner() {
        return owner;
    }
}
