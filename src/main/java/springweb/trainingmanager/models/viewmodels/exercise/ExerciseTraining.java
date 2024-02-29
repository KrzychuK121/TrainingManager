package springweb.trainingmanager.models.viewmodels.exercise;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;
import springweb.trainingmanager.models.entities.Exercise;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ExerciseTraining {

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
    @DateTimeFormat(pattern = "HH:mm:ss")
    private LocalTime time;

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

    public int getId() {
        return id;
    }

    void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name){
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
