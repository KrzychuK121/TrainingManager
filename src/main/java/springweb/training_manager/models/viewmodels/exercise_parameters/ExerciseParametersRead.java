package springweb.training_manager.models.viewmodels.exercise_parameters;

import springweb.training_manager.models.entities.ExerciseParameters;
import springweb.training_manager.models.schemas.ExerciseParametersSchema;

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
}
