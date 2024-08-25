package springweb.training_manager.models.viewmodels.training_routine;

import springweb.training_manager.models.entities.Weekdays;
import springweb.training_manager.models.viewmodels.training_schedule.TrainingScheduleRead;

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
