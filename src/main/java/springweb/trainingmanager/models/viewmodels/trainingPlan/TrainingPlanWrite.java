package springweb.trainingmanager.models.viewmodels.trainingPlan;

import springweb.trainingmanager.models.entities.TrainingPlan;
import springweb.trainingmanager.models.entities.User;

public class TrainingPlanWrite {
    private int trainingId;
    private String trainingTime;

    public TrainingPlanWrite() {
    }
    public TrainingPlanWrite(int trainingId, String trainingTime) {
        this.trainingId = trainingId;
        this.trainingTime = trainingTime;
    }

    public int getTrainingId() {
        return trainingId;
    }
    public void setTrainingId(int trainingId) {
        this.trainingId = trainingId;
    }
    public String getTrainingTime() {
        return trainingTime;
    }
    public void setTrainingTime(String trainingTime) {
        this.trainingTime = trainingTime;
    }

}
