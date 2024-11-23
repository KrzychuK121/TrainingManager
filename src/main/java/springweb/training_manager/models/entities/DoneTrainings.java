package springweb.training_manager.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import springweb.training_manager.models.composite_ids.DoneTrainingsId;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "done_training_register")
@IdClass(DoneTrainingsId.class)
public class DoneTrainings {
    @Id
    @Column(name = "routine_id")
    private int routineId;
    @Id
    @Column(name = "training_id")
    private int trainingId;
    @Setter
    private LocalDateTime startDate;
    @Setter
    private LocalDateTime endDate;

    @Setter
    @ManyToOne
    private TrainingRoutine routine;
    @Setter
    @ManyToOne
    private Training training;

    public void setId(DoneTrainingsId id) {
        this.routineId = id.getRoutineId();
        this.trainingId = id.getTrainingId();
    }

    public DoneTrainingsId getId() {
        return new DoneTrainingsId(routineId, trainingId);
    }


}
