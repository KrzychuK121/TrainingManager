package springweb.trainingmanager.models.viewmodels.trainingPlan;

import springweb.trainingmanager.models.entities.TrainingPlan;
import springweb.trainingmanager.models.entities.Weekdays;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrainingPlansWrite {
    private Map<Weekdays, Integer> schedules;

    public TrainingPlansWrite() {
        schedules = new HashMap<>();
    }
    public TrainingPlansWrite(Map<Weekdays, Integer> schedules) {
        this.schedules = schedules;
    }

    public void setSchedules(Map<Weekdays, Integer> schedules) {
        this.schedules = schedules;
    }

    public Map<Weekdays, Integer> getSchedules() {
        return schedules;
    }

    public void add(Weekdays weekday, int trainingId){
        schedules.put(weekday, trainingId);
    }
    public void add(Weekdays weekday){
        schedules.put(weekday, 0);
    }
}
