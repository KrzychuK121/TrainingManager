package springweb.training_manager.models.entities;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import springweb.training_manager.models.schemas.TrainingSchema;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "training")
public class Training extends TrainingSchema {
    @ManyToMany
    @JoinTable(
        name = "training_exercise",
        joinColumns = @JoinColumn(name = "training_id"),
        inverseJoinColumns = @JoinColumn(name = "exercise_id")
    )
    @Valid
    private List<Exercise> exercises = new ArrayList<>();

    @ManyToMany(mappedBy = "trainings")
    private Set<User> users = new HashSet<>();

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Might be used in PUT but NOT PATCH!!
    public void copy(Training toCopy) {
        this.title = toCopy.title;
        this.description = toCopy.description;
        this.exercises = toCopy.exercises == null || toCopy.exercises.isEmpty() ?
            null :
            toCopy.exercises;
    }
}
