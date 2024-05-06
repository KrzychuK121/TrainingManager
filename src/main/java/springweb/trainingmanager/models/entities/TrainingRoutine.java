package springweb.trainingmanager.models.entities;

import jakarta.persistence.*;
import springweb.trainingmanager.models.schemas.TrainingRoutineSchema;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "training_routine")
public class TrainingRoutine extends TrainingRoutineSchema {

    @ManyToMany
    @JoinTable(
        name = "training_routine_schedule",
        joinColumns = { @JoinColumn(name = "routine_id", referencedColumnName = "id") },
        inverseJoinColumns = {
            @JoinColumn(name = "training_id", referencedColumnName = "training_id"),
            @JoinColumn(name = "weekday", referencedColumnName = "weekday")
        },
        uniqueConstraints = { @UniqueConstraint(columnNames = { "routine_id", "weekday" }) }
    )
    private Set<TrainingSchedule> schedules = new HashSet<>();
    public TrainingRoutine() {
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Set<TrainingSchedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(Set<TrainingSchedule> schedules) {
        this.schedules = schedules;
    }
}
