package springweb.trainingmanager.models.viewmodels.trainingRoutine;

import springweb.trainingmanager.models.entities.Weekdays;
import springweb.trainingmanager.models.viewmodels.trainingSchedule.TrainingScheduleRead;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TrainingRoutineReadIndex {
    private final boolean active;
    private final Map<Weekdays, TrainingScheduleRead> schedules = new HashMap<>();

    public TrainingRoutineReadIndex(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }
    public void putSchedule(Weekdays weekday, TrainingScheduleRead toAdd){
        schedules.put(weekday, toAdd);
    }
    public TrainingScheduleRead getSchedule(String weekday){
        return schedules.get(Weekdays.valueOf(weekday));
    }
    public Map<Weekdays, TrainingScheduleRead> getSchedules() {
        return schedules;
    }
    public boolean containsKey(String weekday){
        return schedules.containsKey(Weekdays.valueOf(weekday));
    }
}
