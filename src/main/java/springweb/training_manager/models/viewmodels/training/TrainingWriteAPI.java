package springweb.training_manager.models.viewmodels.training;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class TrainingWriteAPI {
    private @Valid TrainingWrite toSave;
    private String[] selectedExercises;
}
