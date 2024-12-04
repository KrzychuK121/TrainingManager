package springweb.training_manager.models.viewmodels.exercise;

import lombok.Getter;
import springweb.training_manager.models.entities.BodyPart;
import springweb.training_manager.models.entities.Exercise;
import springweb.training_manager.models.schemas.ExerciseSchema;
import springweb.training_manager.models.viewmodels.training.TrainingExerciseVM;

import java.time.LocalTime;
import java.util.List;

@Getter
public class ExerciseRead extends ExerciseSchema {
    private final List<TrainingExerciseVM> trainings;
    private final String bodyPartDesc;
    private final int parametersId;
    private final int rounds;
    private final int repetition;
    private final short weights;
    private final LocalTime time;
    private final boolean exercisePrivate;

    public ExerciseRead(Exercise exercise) {
        super(
            exercise.getId(),
            exercise.getName(),
            exercise.getDescription(),
            exercise.getBodyPart(),
            exercise.getDefaultBurnedKcal()
        );
        this.parametersId = exercise.getParameters()
            .getId();
        this.rounds = exercise.getParameters()
            .getRounds();
        this.repetition = exercise.getParameters()
            .getRepetition();
        this.weights = exercise.getParameters()
            .getWeights();
        this.time = exercise.getParameters()
            .getTime();
        this.bodyPartDesc = BodyPart.getBodyDesc(bodyPart);
        this.exercisePrivate = exercise.getOwner() != null;
        this.trainings = TrainingExerciseVM.toTrainingExerciseVMList(exercise.getTrainings());
    }
}
