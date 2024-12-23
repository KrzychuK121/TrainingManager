package springweb.training_manager.models.viewmodels.workout_assistant;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
public class BodyPartWorkoutStatistics {
    @Range(min = 1, max = 5)
    private int advanceLevel;
    @Range(min = 0, max = 5)
    private int lastWorkout;
}
