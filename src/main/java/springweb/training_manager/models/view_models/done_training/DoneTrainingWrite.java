package springweb.training_manager.models.view_models.done_training;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import springweb.training_manager.models.view_models.done_exercise.DoneExerciseWrite;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class DoneTrainingWrite {
    private int routineId;
    private int trainingId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private List<DoneExerciseWrite> doneExercises;
}
