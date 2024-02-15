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

public class ExerciseRead {
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
    private List<TrainingExercise> trainings = new ArrayList<>();

    public ExerciseRead(Exercise exercise) {
        this.name = exercise.getName();
        this.description = exercise.getDescription();
        this.rounds = exercise.getRounds();
        this.repetition = exercise.getRepetition();
        this.time = exercise.getTime();
        this.trainings = TrainingExercise.toTrainingExerciseList(exercise.getTrainings());
    }

    public static List<ExerciseRead> toExerciseReadList(final List<Exercise> list){
        List<ExerciseRead> result = new ArrayList<>(list.size());
        list.forEach(exercise -> result.add(new ExerciseRead(exercise)));
        return result;
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

    public List<TrainingExercise> getTrainings() {
        return trainings;
    }

    public void setTrainings(List<TrainingExercise> trainings) {
        this.trainings = trainings;
    }
}
