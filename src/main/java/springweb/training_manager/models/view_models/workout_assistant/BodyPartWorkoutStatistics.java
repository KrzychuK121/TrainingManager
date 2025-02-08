package springweb.training_manager.models.view_models.workout_assistant;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
public class BodyPartWorkoutStatistics {
    public static final int MIN_ADVANCE_LEVEL = 1;
    @Range(min = MIN_ADVANCE_LEVEL, max = 5)
    private int advanceLevel;
    @Range(min = 0, max = 5)
    private int lastWorkout;
}
