package springweb.training_manager.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import springweb.training_manager.models.schemas.Identificable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "done_exercise_register")
public class DoneExercise implements Identificable<Integer> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotNull
    private int doneSeries;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(
        name = "done_training_register_id",
        referencedColumnName = "id"
    )
    private DoneTraining doneTraining;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(
        name = "training_exercise_id",
        referencedColumnName = "id"
    )
    private TrainingExercise trainingExercise;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public Integer defaultId() {
        return 0;
    }
}
