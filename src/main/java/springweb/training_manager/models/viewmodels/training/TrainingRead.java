package springweb.training_manager.models.viewmodels.training;

import lombok.Getter;
import springweb.training_manager.models.entities.Training;
import springweb.training_manager.models.schemas.TrainingSchema;
import springweb.training_manager.models.viewmodels.exercise.ExerciseTraining;

import java.util.ArrayList;
import java.util.List;

@Getter
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

    public static List<TrainingRead> toTrainingReadList(final List<Training> list) {
        List<TrainingRead> result = new ArrayList<>(list.size());
        list.forEach(training -> result.add(new TrainingRead(training)));
        return result;
    }
}
