package springweb.training_manager.models.view_models.training_plan;

import lombok.AllArgsConstructor;
import lombok.Getter;
import springweb.training_manager.models.entities.TrainingPlan;

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
