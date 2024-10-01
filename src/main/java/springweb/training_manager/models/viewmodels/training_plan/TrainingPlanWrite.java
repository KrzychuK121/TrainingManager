package springweb.training_manager.models.viewmodels.training_plan;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TrainingPlanWrite {
    private int trainingId;
    private String trainingTime;
}
