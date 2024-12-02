package springweb.training_manager.models.schemas;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import springweb.training_manager.models.composite_ids.TrainingPlanId;

import java.time.LocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@IdClass(TrainingPlanId.class)
@MappedSuperclass
public class TrainingPlanSchema implements Identificable<TrainingPlanId> {
    public static final String PLAN_TIME_NOT_NULL_MESSAGE = "Podaj czas treningu.";
    @Id
    @Column(name = "routine_id")
    protected int trainingRoutineId;
    @Id
    @Column(name = "schedule_id")
    protected int trainingScheduleId;
    @NotNull(message = PLAN_TIME_NOT_NULL_MESSAGE)
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
