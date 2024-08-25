package springweb.training_manager.models.schemas;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.MappedSuperclass;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;

@IdClass(TrainingPlanId.class)
@MappedSuperclass
public class TrainingPlanSchema {
    @Id
    @Column(name = "routine_id")
    protected int trainingRoutineId;
    @Id
    @Column(name = "schedule_id")
    protected int trainingScheduleId;
    @DateTimeFormat(pattern = "hh:mm")
    protected LocalTime trainingTime;

    public TrainingPlanSchema() {
    }


    public int getTrainingRoutineId() {
        return trainingRoutineId;
    }
    public int getTrainingScheduleId() {
        return trainingScheduleId;
    }
    public TrainingPlanId getId(){
        return new TrainingPlanId(trainingRoutineId, trainingScheduleId);
    }
    public LocalTime getTrainingTime() {
        return trainingTime;
    }


}
