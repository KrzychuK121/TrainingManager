package springweb.trainingmanager.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Entity
@Table(name = "exercise")
public class Exercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotBlank(message = "Nazwa ćwiczenia jest wymagana")
    @Length(min = 3, max = 100, message = "Nazwa ćwiczenia musi mieścić się między 3 a 100 znaków")
    private String name;
    @NotBlank(message = "Opis nie może być pusty")
    @Length(min = 3, max = 300, message = "Opis musi mieścić się między 3 a 300 znaków")
    private String description;
    // ile serii
    @Range(min = 1, max = 10, message = "Ilość serii musi mieścić się między 1 a 10")
    private int rounds;
    // powtórzenia
    @Range(min = 0, max = 100, message = "Ilość powtórzeń musi mieścić się między 0 a 100")
    private int repetition;
    @DateTimeFormat(pattern = "mm:ss")
    private LocalTime time;
    @ManyToMany(mappedBy = "exercises")
    @Valid
    @JsonIgnore
    private List<Training> trainings = new ArrayList<>();

    public Exercise(){ }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getRounds() {
        return rounds;
    }

    public void setRounds(int rounds) {
        this.rounds = rounds;
    }

    public int getRepetition() {
        return repetition;
    }

    public void setRepetition(int repetition) {
        this.repetition = repetition;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public List<Training> getTrainings() {
        return trainings;
    }

    public void setTrainings(List<Training> training) {
        this.trainings = training;
    }

    // Might be used in PUT but NOT PATCH!!
    public void copy(Exercise toEdit) {
        name = toEdit.name;
        description = toEdit.description;
        rounds = toEdit.rounds;
        repetition = toEdit.repetition;
        time = toEdit.time;
        trainings = toEdit.trainings == null || toEdit.trainings.isEmpty() ?
            null :
            toEdit.trainings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Exercise exercise = (Exercise) o;
        return id == exercise.id &&
        rounds == exercise.rounds &&
        repetition == exercise.repetition &&
        Objects.equals(name, exercise.name) &&
        Objects.equals(description, exercise.description) &&
        Objects.equals(time, exercise.time);
    }
}
