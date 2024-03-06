package springweb.trainingmanager.models.viewmodels.training;

import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;
import springweb.trainingmanager.models.entities.Exercise;
import springweb.trainingmanager.models.entities.Training;
import springweb.trainingmanager.models.schemas.TrainingSchema;
import springweb.trainingmanager.models.viewmodels.exercise.ExerciseRead;
import springweb.trainingmanager.models.viewmodels.exercise.ExerciseTraining;

import java.util.ArrayList;
import java.util.List;

public class TrainingRead extends TrainingSchema {
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

    public void setTitle(String title) {
        this.title = title;
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
