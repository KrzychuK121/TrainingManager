package springweb.training_manager.models.entities;

import jakarta.persistence.*;

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
    Training training;
    @ManyToOne
    @JoinColumn(
        name = "exercise_id",
        referencedColumnName = "id"
    )
    Exercise exercise;
    @ManyToOne
    @JoinColumn(
        name = "parameters_id",
        referencedColumnName = "id"
    )
    ExerciseParameters parameters;
}
