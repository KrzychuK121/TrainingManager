package springweb.training_manager.models.schemas;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TrainingPlanId implements Serializable {
    private int trainingRoutineId;
    private int trainingScheduleId;
}
