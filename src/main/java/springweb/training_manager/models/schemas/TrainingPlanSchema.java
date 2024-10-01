package springweb.training_manager.models.schemas;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;

@Getter
@NoArgsConstructor
@IdClass(TrainingPlanId.class)
@MappedSuperclass
public class TrainingPlanSchema implements Identificable<TrainingPlanId> {
    @Id
    @Column(name = "routine_id")
    protected int trainingRoutineId;
    @Id
    @Column(name = "schedule_id")
    protected int trainingScheduleId;
    @DateTimeFormat(pattern = "hh:mm")
    protected LocalTime trainingTime;

    @Override
    public TrainingPlanId getId() {
        return new TrainingPlanId(trainingRoutineId, trainingScheduleId);
    }

    @Override
    public TrainingPlanId defaultId() {
        return null;
    }
}
