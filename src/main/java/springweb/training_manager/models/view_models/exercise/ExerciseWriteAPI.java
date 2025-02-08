package springweb.training_manager.models.view_models.exercise;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ExerciseWriteAPI {
    private @Valid ExerciseWrite toSave;
    private String[] selectedTrainings;
}
