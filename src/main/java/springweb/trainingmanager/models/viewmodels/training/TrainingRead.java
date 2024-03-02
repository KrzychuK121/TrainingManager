package springweb.trainingmanager.models.viewmodels.training;

import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;
import springweb.trainingmanager.models.entities.Exercise;
import springweb.trainingmanager.models.entities.Training;
import springweb.trainingmanager.models.viewmodels.exercise.ExerciseRead;
import springweb.trainingmanager.models.viewmodels.exercise.ExerciseTraining;

import java.util.ArrayList;
import java.util.List;

public class TrainingRead {
    private final int id;
    @NotBlank(message = "Tytuł treningu jest wymagany")
    @Length(min = 3, max = 100, message = "Tytuł musi mieścić się między 3 a 100 znaków")
    protected String title;
    @NotBlank(message = "Opis nie może być pusty")
    @Length(min = 3, max = 300, message = "Opis musi mieścić się między 3 a 300 znaków")
    protected String description;
    protected List<ExerciseTraining> exercises;

    public TrainingRead(Training training) {
        this.id = training.getId();
        this.title = training.getTitle();
        this.description = training.getDescription();
        this.exercises = ExerciseTraining.toExerciseTrainingList(
            training.getExercises()
        );
    }

    public static List<TrainingRead> toTrainingReadList(final List<Training> list){
        List<TrainingRead> result = new ArrayList<>(list.size());
        list.forEach(training -> result.add(new TrainingRead(training)));
        return result;
    }

    public int getId() {
        return id;
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
}
