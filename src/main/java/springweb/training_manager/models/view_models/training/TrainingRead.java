package springweb.training_manager.models.view_models.training;

import lombok.Getter;
import springweb.training_manager.models.entities.Training;
import springweb.training_manager.models.entities.TrainingExercise;
import springweb.training_manager.models.schemas.TrainingSchema;
import springweb.training_manager.models.view_models.exercise.ExerciseTraining;

import java.util.List;

@Getter
public class TrainingRead extends TrainingSchema {
    protected List<ExerciseTraining> exercises;
    private final boolean trainingPrivate;

    public TrainingRead(Training training) {
        super(
            training.getId(),
            training.getTitle(),
            training.getDescription()
        );
        this.exercises = toExerciseTrainingList(
            training.getTrainingExercises()
        );
        this.trainingPrivate = training.getOwner() != null;
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
