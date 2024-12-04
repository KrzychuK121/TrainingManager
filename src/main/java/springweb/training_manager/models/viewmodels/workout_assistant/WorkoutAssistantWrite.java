package springweb.training_manager.models.viewmodels.workout_assistant;

import lombok.Setter;
import lombok.ToString;

@Setter
@ToString
public class WorkoutAssistantWrite {
    private MuscleGrowWrite muscleGrow;
    private WeightReductionWrite weightReduction;
    private int workoutDays;
}
