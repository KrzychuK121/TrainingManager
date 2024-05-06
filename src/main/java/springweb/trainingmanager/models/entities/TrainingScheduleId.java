package springweb.trainingmanager.models.entities;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.io.Serializable;
import java.util.Objects;

public class TrainingScheduleId implements Serializable {
    private int trainingId;
    private Weekdays weekday;

    public TrainingScheduleId() {
    }

    public TrainingScheduleId(int trainingId, Weekdays weekdays) {
        this.trainingId = trainingId;
        this.weekday = weekdays;
    }

    public int getTrainingId() {
        return trainingId;
    }

    public void setTrainingId(int trainingId) {
        this.trainingId = trainingId;
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
        TrainingScheduleId that = (TrainingScheduleId) o;
        return trainingId == that.trainingId && weekday == that.weekday;
    }

    @Override
    public int hashCode() {
        return Objects.hash(trainingId, weekday);
    }
}
