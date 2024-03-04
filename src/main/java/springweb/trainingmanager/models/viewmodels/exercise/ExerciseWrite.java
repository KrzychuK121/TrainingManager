package springweb.trainingmanager.models.viewmodels.exercise;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;
import springweb.trainingmanager.models.entities.Exercise;
import springweb.trainingmanager.models.viewmodels.training.TrainingExercise;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ExerciseWrite {
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
    private List<TrainingExercise> trainings = new ArrayList<>();

    public ExerciseWrite() {
    }

    public static List<Exercise> toExerciseList(final List<ExerciseWrite> list){
        List<Exercise> result = new ArrayList<>(list.size());
        list.forEach(exerciseWrite -> {
            var toSave = new Exercise();

            toSave.setName(exerciseWrite.name);
            toSave.setDescription(exerciseWrite.description);
            toSave.setRounds(exerciseWrite.rounds);
            toSave.setRepetition(exerciseWrite.repetition);
            toSave.setTime(exerciseWrite.time);
            toSave.setTrainings(
                TrainingExercise.toTrainingList(
                    exerciseWrite.trainings
                )
            );

            result.add(toSave);
        });

        return result;
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

    public List<TrainingExercise> getTrainings() {
        return trainings;
    }

    public void setTrainings(List<TrainingExercise> trainings) {
        this.trainings = trainings;
    }

    public Exercise toExercise(){
        var toReturn = new Exercise();

        toReturn.setName(name);
        toReturn.setDescription(description);
        toReturn.setRounds(rounds);
        toReturn.setRepetition(repetition);
        toReturn.setTime(time);
        if(trainings != null)
            toReturn.setTrainings(TrainingExercise.toTrainingList(trainings));

        return toReturn;
    }
}
