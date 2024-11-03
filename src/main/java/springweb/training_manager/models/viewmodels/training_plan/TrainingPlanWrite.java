package springweb.training_manager.models.viewmodels.training_plan;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import springweb.training_manager.models.schemas.TrainingPlanSchema;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TrainingPlanWrite {
    private int trainingId;
    @NotBlank(message = TrainingPlanSchema.PLAN_TIME_NOT_NULL_MESSAGE)
    @NotNull(message = TrainingPlanSchema.PLAN_TIME_NOT_NULL_MESSAGE)
    private String trainingTime;
}
