package springweb.training_manager.models.view_models.workout_assistant;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class WeightReductionWrite {
    private float bmr;
    private float tdee;
    private float caloriesReduction;
    private int dailyKcalReduction;
}
