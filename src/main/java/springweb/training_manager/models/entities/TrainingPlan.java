package springweb.training_manager.models.entities;

import jakarta.persistence.*;
import springweb.training_manager.models.schemas.TrainingPlanId;
import springweb.training_manager.models.schemas.TrainingPlanSchema;

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
        final TrainingSchedule schedule,
        final LocalTime trainingTime
    ){
        trainingRoutine = new TrainingRoutine();
        trainingRoutine.setOwner(owner);

        trainingSchedule = schedule;
        this.trainingTime = trainingTime;
    }

    public TrainingPlan(
        final TrainingRoutine routine,
        final TrainingSchedule schedule,
        final LocalTime trainingTime
    ){
        trainingRoutine = routine;
        trainingSchedule = schedule;
        this.trainingTime = trainingTime;
    }



    public void setId(TrainingPlanId id){
        trainingRoutineId = id.getTrainingRoutineId();
        trainingScheduleId = id.getTrainingScheduleId();
    }

    public void setTrainingTime(LocalTime trainingTime) {
        this.trainingTime = trainingTime;
    }
    public void setTrainingRoutineId(int trainingRoutineId) {
        this.trainingRoutineId = trainingRoutineId;
    }
    public void setTrainingScheduleId(int trainingScheduleId) {
        this.trainingScheduleId = trainingScheduleId;
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
