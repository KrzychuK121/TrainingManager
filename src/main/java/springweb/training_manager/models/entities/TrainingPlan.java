package springweb.training_manager.models.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import springweb.training_manager.models.schemas.TrainingPlanId;
import springweb.training_manager.models.schemas.TrainingPlanSchema;

import java.time.LocalTime;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "training_plan")
public class TrainingPlan extends TrainingPlanSchema {
    @ManyToOne
    @JoinColumn(
        name = "schedule_id",
        referencedColumnName = "id",
        insertable = false,
        updatable = false
    )
    private TrainingSchedule trainingSchedule;
    @ManyToOne
    @JoinColumn(
        name = "routine_id",
        referencedColumnName = "id",
        insertable = false,
        updatable = false
    )
    private TrainingRoutine trainingRoutine;

    public TrainingPlan(
        final User owner,
        final TrainingSchedule schedule,
        final LocalTime trainingTime
    ) {
        trainingRoutine = new TrainingRoutine();
        trainingRoutine.setOwner(owner);

        trainingSchedule = schedule;
        this.trainingTime = trainingTime;
    }

    public TrainingPlan(
        final TrainingRoutine routine,
        final TrainingSchedule schedule,
        final LocalTime trainingTime
    ) {
        trainingRoutine = routine;
        trainingSchedule = schedule;
        this.trainingTime = trainingTime;
    }


    public void setId(TrainingPlanId id) {
        trainingRoutineId = id.getTrainingRoutineId();
        trainingScheduleId = id.getTrainingScheduleId();
    }

    public void setTrainingTime(LocalTime trainingTime) {
        this.trainingTime = trainingTime;
    }

}
