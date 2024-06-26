package springweb.trainingmanager.models.entities;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;
import springweb.trainingmanager.models.schemas.TrainingSchema;

import java.util.*;

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

    public Training(){ }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Exercise> getExercises() {
        return exercises;
    }

    public void setExercises(List<Exercise> exercises) {
        this.exercises = exercises;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    // Might be used in PUT but NOT PATCH!!
    public void copy(Training toCopy){
        this.title = toCopy.title;
        this.description = toCopy.description;
        this.exercises = toCopy.exercises == null || toCopy.exercises.isEmpty() ?
            null :
            toCopy.exercises;
    }
}
