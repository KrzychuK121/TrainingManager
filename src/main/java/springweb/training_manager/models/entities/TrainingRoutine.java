package springweb.training_manager.models.entities;

import jakarta.persistence.*;
import springweb.training_manager.models.schemas.TrainingRoutineSchema;

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
    public TrainingRoutine(User owner) {
        this.owner = owner;
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
