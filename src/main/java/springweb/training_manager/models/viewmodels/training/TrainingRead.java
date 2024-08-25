package springweb.training_manager.models.viewmodels.training;

import springweb.training_manager.models.entities.Training;
import springweb.training_manager.models.schemas.TrainingSchema;
import springweb.training_manager.models.viewmodels.exercise.ExerciseTraining;

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
