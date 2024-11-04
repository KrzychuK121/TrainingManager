package springweb.training_manager.models.schemas;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import springweb.training_manager.models.entities.TrainingRoutine;
import springweb.training_manager.models.entities.TrainingSchedule;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TrainingPlanId implements Serializable {
    private int trainingRoutineId;
    private int trainingScheduleId;

    public TrainingPlanId(
        TrainingRoutine routine,
        TrainingSchedule schedule
    ) {
        this(
            routine != null
                ? routine.getId()
                : 0,
            schedule != null
                ? schedule.getId()
                : 0
        );
    }
}
