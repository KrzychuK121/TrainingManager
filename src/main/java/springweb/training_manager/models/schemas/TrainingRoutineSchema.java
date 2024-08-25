package springweb.training_manager.models.schemas;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;

@MappedSuperclass
public class TrainingRoutineSchema {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    protected int id;
    @NotNull
    protected boolean active = false;

    public TrainingRoutineSchema(){

    }

    public int getId() {
        return id;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrainingRoutineSchema that = (TrainingRoutineSchema) o;
        return active == that.active;
    }
}
