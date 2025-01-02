package springweb.training_manager.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "training_exercise")
public class TrainingExercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    @JoinColumn(
        name = "training_id",
        referencedColumnName = "id"
    )
    private Training training;
    @ManyToOne
    @JoinColumn(
        name = "exercise_id",
        referencedColumnName = "id"
    )
    private Exercise exercise;
    @ManyToOne
    @JoinColumn(
        name = "parameters_id",
        referencedColumnName = "id"
    )
    private ExerciseParameters parameters;

    public TrainingExercise(
        Training training,
        Exercise exercise,
        ExerciseParameters parameters
    ) {
        this(
            0,
            training,
            exercise,
            parameters
        );
    }
}
