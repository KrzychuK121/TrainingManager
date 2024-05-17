package springweb.trainingmanager.models.entities;

import jakarta.persistence.*;
import springweb.trainingmanager.models.schemas.TrainingPlanId;
import springweb.trainingmanager.models.schemas.TrainingPlanSchema;

import java.time.LocalTime;

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

    public TrainingPlan() {
    }

    public TrainingPlan(
        final User owner,
        final TrainingSchedule schedule
    ){
        trainingRoutine = new TrainingRoutine();
        trainingRoutine.setOwner(owner);

        trainingSchedule = schedule;
    }

    public TrainingPlan(
        final TrainingRoutine routine,
        final TrainingSchedule schedule
    ){
        trainingRoutine = routine;
        trainingSchedule = schedule;
    }



    public void setId(TrainingPlanId id){
        trainingRoutineId = id.getTrainingRoutineId();
        trainingScheduleId = id.getTrainingScheduleId();
    }

    public void setTrainingTime(LocalTime trainingTime) {
        this.trainingTime = trainingTime;
    }
    public TrainingSchedule getTrainingSchedule() {
        return trainingSchedule;
    }
    public void setTrainingSchedule(TrainingSchedule trainingSchedule) {
        this.trainingSchedule = trainingSchedule;
    }
    public TrainingRoutine getTrainingRoutine() {
        return trainingRoutine;
    }
    public void setTrainingRoutine(TrainingRoutine trainingRoutine) {
        this.trainingRoutine = trainingRoutine;
    }
}
