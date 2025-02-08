package springweb.training_manager.models.view_models.done_exercise;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class DoneExerciseWrite {
    private int exerciseId;
    @NotNull
    private int doneSeries;
}
