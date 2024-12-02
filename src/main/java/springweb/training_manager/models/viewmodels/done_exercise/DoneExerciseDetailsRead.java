package springweb.training_manager.models.viewmodels.done_exercise;

import lombok.Getter;
import springweb.training_manager.models.entities.DoneExercise;

@Getter
public class DoneExerciseDetailsRead {
    private final int exerciseId;
    private final int doneSeries;

    public DoneExerciseDetailsRead(DoneExercise doneExercise) {
        exerciseId = doneExercise.getTrainingExercise()
            .getExercise()
            .getId();
        doneSeries = doneExercise.getDoneSeries();
    }
}
