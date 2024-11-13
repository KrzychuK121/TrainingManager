package springweb.training_manager.models.viewmodels.training;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class TrainingWriteAPI {
    private @Valid TrainingWrite toSave;
    private List<@Valid SelectedExerciseWrite> selectedExercises;
}
