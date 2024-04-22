package springweb.trainingmanager.models.schemas;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;
import springweb.trainingmanager.models.entities.Difficulty;
import springweb.trainingmanager.models.entities.Training;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@MappedSuperclass
public abstract class ExerciseSchema {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected int id;
    @NotBlank(message = "Nazwa ćwiczenia jest wymagana")
    @Length(min = 3, max = 100, message = "Nazwa ćwiczenia musi mieścić się między 3 a 100 znaków")
    protected String name;
    @NotBlank(message = "Opis nie może być pusty")
    @Length(min = 3, max = 300, message = "Opis musi mieścić się między 3 a 300 znaków")
    protected String description;
    // ile serii
    @Range(min = 1, max = 10, message = "Ilość serii musi mieścić się między 1 a 10")
    protected int rounds;
    // powtórzenia
    @Range(min = 0, max = 100, message = "Ilość powtórzeń musi mieścić się między 0 a 100")
    protected int repetition;

    //protected float weights;
    @DateTimeFormat(pattern = "mm:ss")
    protected LocalTime time;
    @Enumerated(EnumType.STRING)
    protected Difficulty difficulty;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getRounds() {
        return rounds;
    }

    public int getRepetition() {
        return repetition;
    }

    public LocalTime getTime() {
        return time;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExerciseSchema exercise = (ExerciseSchema) o;
        return id == exercise.id &&
                rounds == exercise.rounds &&
                repetition == exercise.repetition &&
                Objects.equals(name, exercise.name) &&
                Objects.equals(description, exercise.description) &&
                Objects.equals(time, exercise.time) &&
                difficulty == exercise.difficulty;
    }

}
