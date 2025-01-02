package springweb.training_manager.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import springweb.training_manager.models.schemas.Identificable;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "training_schedule")
public class TrainingSchedule implements Identificable<Integer> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "training_id")
    private int trainingId;
    @Enumerated(EnumType.STRING)
    private Weekdays weekday;
    @ManyToOne
    @JoinColumn(
        name = "training_id",
        referencedColumnName = "id",
        insertable = false,
        updatable = false
    )
    private Training training;

    public TrainingSchedule(int trainingId, Weekdays weekday) {
        this.trainingId = trainingId;
        this.weekday = weekday;
    }

    public TrainingSchedule(Training training, Weekdays weekday) {
        this(
            training.getId(),
            weekday
        );

        this.training = training;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public Integer defaultId() {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrainingSchedule that = (TrainingSchedule) o;
        return id == that.id &&
            trainingId == that.trainingId &&
            weekday == that.weekday &&
            Objects.equals(training, that.training);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trainingId, weekday, training);
    }
}