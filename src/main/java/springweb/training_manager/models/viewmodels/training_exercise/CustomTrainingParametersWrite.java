package springweb.training_manager.models.viewmodels.training_exercise;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import springweb.training_manager.models.entities.ExerciseParameters;
import springweb.training_manager.models.viewmodels.exercise.ExerciseTraining;
import springweb.training_manager.models.viewmodels.exercise_parameters.ExerciseParametersWrite;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CustomTrainingParametersWrite {
    private ExerciseTraining exerciseWrite;
    @Valid
    private ExerciseParametersWrite parameters;

    public ExerciseParameters getParameters() {
        if (parameters != null)
            return parameters.toEntity();

        return exerciseWrite.getExerciseParameters();
    }
}
