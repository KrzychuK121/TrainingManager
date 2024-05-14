package springweb.trainingmanager.models.viewmodels.trainingRoutine;

import springweb.trainingmanager.models.entities.TrainingRoutine;
import springweb.trainingmanager.models.schemas.TrainingRoutineSchema;
import springweb.trainingmanager.models.viewmodels.user.UserRead;

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
