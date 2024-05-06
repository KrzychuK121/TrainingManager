package springweb.trainingmanager.models.entities;

import jakarta.persistence.*;

@Entity
@IdClass(TrainingScheduleId.class)
@Table(name = "training_schedule")
public class TrainingSchedule {
    @Id
    @Column(name = "training_id")
    private int trainingId;
    @Id
    @Enumerated(EnumType.STRING)
    private Weekdays weekday;
    // TODO: Add field responsible for time (when user wants to do his training in specified day)
    @ManyToOne
    @JoinColumn(name = "training_id", referencedColumnName = "id")
    private Training training;

    public TrainingSchedule() {
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


}
