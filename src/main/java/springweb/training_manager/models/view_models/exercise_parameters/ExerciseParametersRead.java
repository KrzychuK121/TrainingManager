package springweb.training_manager.models.view_models.exercise_parameters;

import springweb.training_manager.models.entities.ExerciseParameters;
import springweb.training_manager.models.schemas.ExerciseParametersSchema;

import java.time.LocalTime;

public class ExerciseParametersRead extends ExerciseParametersSchema {
    public ExerciseParametersRead(
        ExerciseParameters parameters
    ) {
        super(
            parameters.getId(),
            parameters.getRounds(),
            parameters.getRepetition(),
            parameters.getWeights(),
            parameters.getTime()
        );
    }

    public ExerciseParametersRead(
        int id,
        int rounds,
        int repetition,
        short weights,
        LocalTime time
    ) {
        super(
            id,
            rounds,
            repetition,
            weights,
            time
        );
    }
}
