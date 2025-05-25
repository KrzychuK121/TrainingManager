package springweb.training_manager.models.view_models.exercise;

import lombok.Getter;
import springweb.training_manager.models.entities.BodyPart;
import springweb.training_manager.models.entities.Exercise;
import springweb.training_manager.models.entities.ExerciseParameters;
import springweb.training_manager.models.schemas.ExerciseSchema;

import java.time.LocalTime;

@Getter
public class ExerciseRead extends ExerciseSchema {
    private final String bodyPartDesc;
    private final int parametersId;
    private final int rounds;
    private final int repetition;
    private final short weights;
    private final LocalTime time;
    private final boolean exercisePrivate;
    private final boolean archived;

    public ExerciseRead(Exercise exercise) {
        super(
            exercise.getId(),
            exercise.getName(),
            exercise.getDescription(),
            exercise.getBodyPart(),
            exercise.getDefaultBurnedKcal()
        );

        ExerciseParameters parameters = exercise.getParameters();
        this.parametersId = parameters.getId();
        this.rounds = parameters.getRounds();
        this.repetition = parameters.getRepetition();
        this.weights = parameters.getWeights();
        this.time = parameters.getTime();
        this.bodyPartDesc = BodyPart.getBodyDesc(bodyPart);
        this.exercisePrivate = exercise.getOwner() != null;
        this.archived = exercise.isArchived();
    }
}
