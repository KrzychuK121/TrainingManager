package springweb.trainingmanager.models.viewmodels.training;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;
import springweb.trainingmanager.models.entities.Exercise;
import springweb.trainingmanager.models.entities.Training;
import springweb.trainingmanager.models.viewmodels.exercise.ExerciseTraining;
import springweb.trainingmanager.models.viewmodels.exercise.ExerciseWrite;

import java.util.ArrayList;
import java.util.List;

public class TrainingWrite {
    @NotBlank(message = "Tytuł treningu jest wymagany")
    @Length(min = 3, max = 100, message = "Tytuł musi mieścić się między 3 a 100 znaków")
    private String title;
    @NotBlank(message = "Opis nie może być pusty")
    @Length(min = 3, max = 300, message = "Opis musi mieścić się między 3 a 300 znaków")
    private String description;
    private List<ExerciseTraining> exercises = new ArrayList<>();

    public TrainingWrite() {

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

    public List<ExerciseTraining> getExercises() {
        return exercises;
    }

    public void setExercises(List<ExerciseTraining> exercises) {
        this.exercises = exercises;
    }

    public Training toTraining(){
        var toReturn = new Training();

        toReturn.setTitle(title);
        toReturn.setDescription(description);
        if(exercises != null)
            toReturn.setExercises(ExerciseTraining.toExerciseList(exercises));

        return toReturn;
    }
}
