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
    private int id;
    @NotNull
    private int doneSeries;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumns({
        @JoinColumn(name = "routine_id", referencedColumnName = "routine_id", nullable = false),
        @JoinColumn(name = "training_id", referencedColumnName = "training_id", nullable = false)
    })
    private DoneTraining doneTraining;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
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
