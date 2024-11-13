package springweb.training_manager.models.viewmodels.training;

import lombok.Getter;
import springweb.training_manager.models.entities.Training;
import springweb.training_manager.models.entities.TrainingExercise;
import springweb.training_manager.models.schemas.TrainingSchema;
import springweb.training_manager.models.viewmodels.exercise.ExerciseTraining;

import java.util.List;

@Getter
public class TrainingRead extends TrainingSchema {
    protected List<ExerciseTraining> exercises;

    public TrainingRead(Training training) {
        super(
            training.getId(),
            training.getTitle(),
            training.getDescription()
        );
        this.exercises = toExerciseTrainingList(
            training.getTrainingExercises()
        );
    }

    public static List<ExerciseTraining> toExerciseTrainingList(List<TrainingExercise> toMap) {
        return toMap.stream()
            .map(
                trainingExercise -> new ExerciseTraining(
                    trainingExercise.getExercise(),
                    trainingExercise.getParameters()
                )
            )
            .toList();
    }
}
