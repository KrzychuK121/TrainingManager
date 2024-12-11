package springweb.training_manager.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import springweb.training_manager.models.schemas.TrainingRoutineSchema;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "training_routine")
public class TrainingRoutine extends TrainingRoutineSchema {
    @ManyToOne
    @JoinColumn(
        name = "owner_id",
        referencedColumnName = "id",
        nullable = false
    )
    private User owner;

    @OneToMany(mappedBy = "trainingRoutine")
    private List<TrainingPlan> plans = new ArrayList<>();

    public void setId(int id) {
        this.id = id;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
