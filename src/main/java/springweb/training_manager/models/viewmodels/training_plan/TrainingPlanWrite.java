package springweb.training_manager.models.viewmodels.training_plan;

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
