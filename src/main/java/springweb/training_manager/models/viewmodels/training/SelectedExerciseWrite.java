package springweb.training_manager.models.viewmodels.training;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import springweb.training_manager.models.viewmodels.exercise_parameters.ExerciseParametersWrite;

@AllArgsConstructor
@Setter
@Getter
public class SelectedExerciseWrite {
    private String selectedId;
    @Valid
    private ExerciseParametersWrite parameters;
}
