package springweb.trainingmanager.models.viewmodels.exercise;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;
import springweb.trainingmanager.models.entities.Exercise;
import springweb.trainingmanager.models.schemas.ExerciseSchema;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ExerciseTraining extends ExerciseSchema {

    public ExerciseTraining(){ }

    public ExerciseTraining(Exercise exercise) {
        this.id = exercise.getId();
        this.name = exercise.getName();
        this.description = exercise.getDescription();
        this.rounds = exercise.getRounds();
        this.repetition = exercise.getRepetition();
        this.time = exercise.getTime();
    }

    public static List<ExerciseTraining> toExerciseTrainingList(final List<Exercise> list){
        if(list == null)
            return null;
        List<ExerciseTraining> result = new ArrayList<>(list.size());
        list.forEach(exercise -> result.add(new ExerciseTraining(exercise)));
        return result;
    }

    public static List<Exercise> toExerciseList(final List<ExerciseTraining> list){
        List<Exercise> result = new ArrayList<>(list.size());
        list.forEach(exerciseTraining -> {
            var toSave = new Exercise();

            toSave.setId(exerciseTraining.id);
            toSave.setName(exerciseTraining.name);
            toSave.setDescription(exerciseTraining.description);
            toSave.setRounds(exerciseTraining.rounds);
            toSave.setRepetition(exerciseTraining.repetition);
            toSave.setTime(exerciseTraining.time);

            result.add(toSave);
        });

        return result;
    }

    void setId(int id) {
        this.id = id;
    }

    public void setName(String name){
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

    public Exercise toExercise(){
        var toReturn = new Exercise();

        toReturn.setName(name);
        toReturn.setDescription(description);
        toReturn.setRounds(rounds);
        toReturn.setRepetition(repetition);
        toReturn.setTime(time);

        return toReturn;
    }

}
