package springweb.trainingmanager.models.schemas;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;
import springweb.trainingmanager.models.entities.BodyPart;
import springweb.trainingmanager.models.entities.Difficulty;

import java.time.LocalTime;
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
    @Column(columnDefinition = "SMALLINT")
    @Range(min = 0, max = 300, message = "Ilość obciążenia musi mieścić się między 0 a 300")
    protected int weights;
    @DateTimeFormat(pattern = "mm:ss")
    protected LocalTime time;
    @NotNull
    @Enumerated(EnumType.STRING)
    protected BodyPart bodyPart;
    @Enumerated(EnumType.STRING)
    protected Difficulty difficulty;

    public ExerciseSchema() {
    }

    public ExerciseSchema(
        String name,
        String description,
        int rounds,
        int repetition,
        int weights,
        LocalTime time,
        BodyPart bodyPart,
        Difficulty difficulty
    ) {
        this.name = name;
        this.description = description;
        this.rounds = rounds;
        this.repetition = repetition;
        this.weights = weights;
        this.time = time;
        this.bodyPart = bodyPart;
        this.difficulty = difficulty;
    }

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

    public int getWeights() {
        return weights;
    }

    public LocalTime getTime() {
        return time;
    }

    public BodyPart getBodyPart(){
        return bodyPart;
    }

    public Difficulty getDifficulty() {
        return difficulty;
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
                bodyPart == exercise.bodyPart &&
                difficulty == exercise.difficulty;
    }

}
