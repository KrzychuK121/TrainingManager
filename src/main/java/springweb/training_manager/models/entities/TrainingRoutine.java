package springweb.training_manager.models.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import springweb.training_manager.models.schemas.TrainingRoutineSchema;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
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

    public void setId(int id) {
        this.id = id;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
