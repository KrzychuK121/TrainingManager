package springweb.training_manager.models.entities;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import springweb.training_manager.models.schemas.ExerciseSchema;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "exercise")
public class Exercise extends ExerciseSchema {
    @ManyToMany(mappedBy = "exercises")
    @Valid
    private List<Training> trainings = new ArrayList<>();

    public Exercise(){ }

    public Exercise(
        String name,
        String description,
        int rounds,
        int repetition,
        short weights,
        LocalTime time,
        BodyPart bodyPart,
        Difficulty difficulty
    ) {
        super(
            name,
            description,
            rounds,
            repetition,
            weights,
            time,
            bodyPart,
            difficulty
        );
    }



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

    public void setWeights(short weights){
        this.weights = weights;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public void setBodyPart(BodyPart bodyPart){
        this.bodyPart = bodyPart;
    }

    public void setDifficulty(Difficulty difficulty){
        this.difficulty = difficulty;
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
        weights = toEdit.weights;
        difficulty = toEdit.difficulty;
        time = toEdit.time;
        bodyPart = toEdit.bodyPart;
        trainings = toEdit.trainings == null || toEdit.trainings.isEmpty() ?
            null :
            toEdit.trainings;
    }
}
