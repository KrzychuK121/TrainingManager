package springweb.training_manager.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import springweb.training_manager.models.schemas.Identificable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "done_training_register")
public class DoneTraining implements Identificable<Integer> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @ManyToOne
    @JoinColumn(
        name = "routine_id",
        referencedColumnName = "id"
    )
    private TrainingRoutine routine;
    @ManyToOne
    @JoinColumn(
        name = "training_id",
        referencedColumnName = "id"
    )
    private Training training;

    @OneToMany(mappedBy = "doneTraining")
    private List<DoneExercise> doneExercises = new ArrayList<>();

    @Override
    public Integer getId() {
        return this.id;
    }

    @Override
    public Integer defaultId() {
        return 0;
    }

    public void addDoneExercise(DoneExercise doneExercise) {
        doneExercises.add(doneExercise);
    }

    public void removeDoneExercise(DoneExercise doneExercise) {
        doneExercises.remove(doneExercise);
    }
}
