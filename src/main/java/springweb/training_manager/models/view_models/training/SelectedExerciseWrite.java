package springweb.training_manager.models.view_models.training;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import springweb.training_manager.models.view_models.exercise_parameters.ExerciseParametersWrite;

@AllArgsConstructor
@Setter
@Getter
public class SelectedExerciseWrite {
    private String selectedId;
    @Valid
    private ExerciseParametersWrite parameters;
}
