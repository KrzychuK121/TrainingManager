package springweb.training_manager.models.viewmodels.training_plan;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import springweb.training_manager.models.entities.TrainingPlan;
import springweb.training_manager.models.schemas.TrainingPlanSchema;

import java.time.LocalTime;

@AllArgsConstructor
@Getter
public class TrainingPlanEditRead {
    private int trainingId;
    private LocalTime trainingTime;

    public TrainingPlanEditRead(TrainingPlan trainingPlan) {
        this(
            trainingPlan.getTrainingSchedule()
                .getTrainingId(),
            trainingPlan.getTrainingTime()
        );
    }
}
