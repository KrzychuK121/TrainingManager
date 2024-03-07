package springweb.trainingmanager.models.entities;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;
import springweb.trainingmanager.models.schemas.ExerciseSchema;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Entity
@Table(name = "exercise")
public class Exercise extends ExerciseSchema {
    @ManyToMany(mappedBy = "exercises")
    @Valid
    private List<Training> trainings = new ArrayList<>();

    public Exercise(){ }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setRounds(int rounds) {
        this.rounds = rounds;
    }

    public void setRepetition(int repetition) {
        this.repetition = repetition;
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
