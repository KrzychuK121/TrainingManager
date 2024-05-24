package springweb.trainingmanager.models.viewmodels.trainingPlan;

import springweb.trainingmanager.models.entities.Weekdays;

import java.util.HashMap;
import java.util.Map;

public class TrainingPlansWrite {
    private Map<Weekdays, TrainingPlanWrite> planWriteMap;

    public TrainingPlansWrite() {
        planWriteMap = new HashMap<>();
    }
    public TrainingPlansWrite(Map<Weekdays, TrainingPlanWrite> planWriteMap) {
        this.planWriteMap = planWriteMap;
    }

    public void setPlanWriteMap(Map<Weekdays, TrainingPlanWrite> planWriteMap) {
        this.planWriteMap = planWriteMap;
    }
    public Map<Weekdays, TrainingPlanWrite> getPlanWriteMap() {
        return planWriteMap;
    }

    public void add(Weekdays weekday, int trainingId, String trainingTime){
        var trainingPlan = new TrainingPlanWrite(trainingId, trainingTime);
        planWriteMap.put(weekday, trainingPlan);
    }
    public void add(Weekdays weekday){
        var trainingPlan = new TrainingPlanWrite(0, null);
        planWriteMap.put(weekday, trainingPlan);
    }
}
