package springweb.training_manager.models.viewmodels.workout_assistant;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;

import java.time.LocalTime;

@Getter
@Setter
@ToString
public class WorkoutAssistantWrite {
    private MuscleGrowWrite muscleGrow;
    private WeightReductionWrite weightReduction;
    @Range(min = 1, max = 7, message = "Liczba dni treningowych musi być w przedziale 1-7")
    private int workoutDays;
    @NotNull
    private LocalTime earliestTrainingStart;
    @NotNull
    private LocalTime latestTrainingStart;
}
