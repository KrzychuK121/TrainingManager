package springweb.trainingmanager.models.schemas;

import java.io.Serializable;

public class TrainingPlanId implements Serializable {
    private int trainingRoutineId;
    private int trainingScheduleId;

    public TrainingPlanId() {
    }

    public TrainingPlanId(
        final int trainingRoutineId,
        final int trainingScheduleId
    ) {
        this.trainingRoutineId = trainingRoutineId;
        this.trainingScheduleId = trainingScheduleId;
    }

    public int getTrainingRoutineId() {
        return trainingRoutineId;
    }

    public void setTrainingRoutineId(int trainingRoutineId) {
        this.trainingRoutineId = trainingRoutineId;
    }

    public int getTrainingScheduleId() {
        return trainingScheduleId;
    }

    public void setTrainingScheduleId(int trainingScheduleId) {
        this.trainingScheduleId = trainingScheduleId;
    }
}
