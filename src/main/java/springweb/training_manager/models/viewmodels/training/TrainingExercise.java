package springweb.training_manager.models.viewmodels.training;

import springweb.training_manager.models.entities.Training;
import springweb.training_manager.models.schemas.TrainingSchema;

import java.util.ArrayList;
import java.util.List;

public class TrainingExercise extends TrainingSchema {

    public TrainingExercise(){
        id = 0;
    }

    public TrainingExercise(Training training, int id) {
        this.id = id;
        this.title = training.getTitle();
        this.description = training.getDescription();
    }

    public static List<TrainingExercise> toTrainingExerciseList(final List<Training> list){
        if(list == null)
            return null;
        List<TrainingExercise> result = new ArrayList<>(list.size());
        list.forEach(training -> result.add(new TrainingExercise(training, training.getId())));
        return result;
    }

    public static List<Training> toTrainingList(final List<TrainingExercise> list){
        List<Training> result = new ArrayList<>(list.size());
        list.forEach(trainingExercise -> {
            var toSave = new Training();

            toSave.setId(trainingExercise.getId());
            toSave.setId(trainingExercise.getId());
            toSave.setTitle(trainingExercise.title);
            toSave.setDescription(trainingExercise.description);

            result.add(toSave);
        });

        return result;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Training toTraining(){
        var toReturn = new Training();

        toReturn.setTitle(title);
        toReturn.setDescription(description);

        return toReturn;
    }
}
