package springweb.training_manager.models.viewmodels.training_routine;

import lombok.Getter;
import springweb.training_manager.models.entities.Weekdays;
import springweb.training_manager.models.viewmodels.training_schedule.TrainingScheduleRead;

import java.util.HashMap;
import java.util.Map;

@Getter
public class TrainingRoutineReadIndex {
    private final int id;
    private final boolean active;
    private final Map<Weekdays, TrainingScheduleRead> schedules = new HashMap<>();

    public TrainingRoutineReadIndex(
        int id,
        boolean active
    ) {
        this.id = id;
        this.active = active;
    }

    public void putSchedule(Weekdays weekday, TrainingScheduleRead toAdd) {
        schedules.put(weekday, toAdd);
    }

    public TrainingScheduleRead getSchedule(String weekday) {
        return schedules.get(Weekdays.valueOf(weekday));
    }

    public boolean containsKey(String weekday) {
        return schedules.containsKey(Weekdays.valueOf(weekday));
    }
}
