package springweb.training_manager.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import springweb.training_manager.models.composite_ids.DoneTrainingId;
import springweb.training_manager.models.schemas.Identificable;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "done_training_register")
@IdClass(DoneTrainingId.class)
public class DoneTraining implements Identificable<DoneTrainingId> {
    @Id
    @Column(name = "routine_id")
    private int routineId;
    @Id
    @Column(name = "training_id")
    private int trainingId;
    @Id
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

    @OneToMany(mappedBy = "doneTraining")
    private List<DoneExercise> doneExercises;

    public void setId(DoneTrainingId id) {
        this.routineId = id.getRoutineId();
        this.trainingId = id.getTrainingId();
        this.startDate = id.getStartDate();
    }

    public DoneTrainingId getId() {
        return new DoneTrainingId(
            routineId,
            trainingId,
            startDate
        );
    }

    @Override
    public DoneTrainingId defaultId() {
        return null;
    }


}
