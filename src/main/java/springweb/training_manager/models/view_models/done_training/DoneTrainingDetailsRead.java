package springweb.training_manager.models.view_models.done_training;

import lombok.Getter;
import springweb.training_manager.models.entities.DoneTraining;
import springweb.training_manager.models.view_models.done_exercise.DoneExerciseDetailsRead;
import springweb.training_manager.models.view_models.training.TrainingRead;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class DoneTrainingDetailsRead {
    private final TrainingRead training;
    private final List<DoneExerciseDetailsRead> doneExercises;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final boolean done;

    public DoneTrainingDetailsRead(DoneTraining doneTraining) {
        training = new TrainingRead(
            doneTraining.getTraining()
        );
        doneExercises = doneTraining.getDoneExercises()
            .stream()
            .map(DoneExerciseDetailsRead::new)
            .toList();
        startDate = doneTraining.getStartDate();
        endDate = doneTraining.getEndDate();
        done = doneTraining.getEndDate() != null;
    }
}
