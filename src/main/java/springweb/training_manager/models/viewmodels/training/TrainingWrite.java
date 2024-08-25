package springweb.training_manager.models.viewmodels.training;

import springweb.training_manager.models.entities.Training;
import springweb.training_manager.models.schemas.TrainingSchema;
import springweb.training_manager.models.viewmodels.exercise.ExerciseTraining;

import java.util.ArrayList;
import java.util.List;

public class TrainingWrite extends TrainingSchema {
    private List<ExerciseTraining> exercises = new ArrayList<>();

    public TrainingWrite() {

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

    public Training toTraining(){
        var toReturn = new Training();

        toReturn.setTitle(title);
        toReturn.setDescription(description);
        if(exercises != null)
            toReturn.setExercises(ExerciseTraining.toExerciseList(exercises));

        return toReturn;
    }
}
