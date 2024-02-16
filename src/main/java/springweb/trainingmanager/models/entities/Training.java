package springweb.trainingmanager.models.entities;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "training")
public class Training {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotBlank(message = "Tytuł treningu jest wymagany")
    @Length(min = 3, max = 100, message = "Tytuł musi mieścić się między 3 a 100 znaków")
    private String title;
    @NotBlank(message = "Opis nie może być pusty")
    @Length(min = 3, max = 300, message = "Opis musi mieścić się między 3 a 300 znaków")
    private String description;
    @ManyToMany
    @JoinTable(
        name = "training_exercise",
        joinColumns = @JoinColumn(name = "training_id"),
        inverseJoinColumns = @JoinColumn(name = "exercise_id")
    )
    @Valid
    private List<Exercise> exercises = new ArrayList<>();

    public Training(){ }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
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

    // Might be used in PUT but NOT PATCH!!
    public void copy(Training toCopy){
        this.title = toCopy.title;
        this.description = toCopy.description;
        this.exercises = toCopy.exercises == null || toCopy.exercises.isEmpty() ?
            null :
            toCopy.exercises;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Training training = (Training) o;
        return id == training.id && Objects.equals(title, training.title) && Objects.equals(description, training.description);
    }
}
