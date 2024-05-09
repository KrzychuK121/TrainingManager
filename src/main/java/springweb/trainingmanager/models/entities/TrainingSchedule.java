package springweb.trainingmanager.models.entities;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "training_schedule")
public class TrainingSchedule {
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

    public TrainingSchedule() {
    }

    public TrainingSchedule(int trainingId, Weekdays weekday){
        this.trainingId = trainingId;
        this.weekday = weekday;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTrainingId() {
        return trainingId;
    }

    public void setTrainingId(int trainingId) {
        this.trainingId = trainingId;
    }

    public Training getTraining() {
        return training;
    }

    public void setTraining(Training training) {
        this.training = training;
    }

    public Weekdays getWeekday() {
        return weekday;
    }

    public void setWeekday(Weekdays weekday) {
        this.weekday = weekday;
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
