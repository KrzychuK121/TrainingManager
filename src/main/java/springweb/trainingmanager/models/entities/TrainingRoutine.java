package springweb.trainingmanager.models.entities;

import jakarta.persistence.*;
import springweb.trainingmanager.models.schemas.TrainingRoutineSchema;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "training_routine")
public class TrainingRoutine extends TrainingRoutineSchema {
    @ManyToOne
    @JoinColumn(
        name = "identity_user_id",
        referencedColumnName = "id",
        nullable = false
    )
    private User owner;

    public TrainingRoutine() {
    }

    public void setId(int id) {
        this.id = id;
    }
    public void setActive(boolean active) {
        this.active = active;
    }
    public User getOwner() {
        return owner;
    }
    public void setOwner(User owner) {
        this.owner = owner;
    }
}
