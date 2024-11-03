package springweb.training_manager.models.viewmodels.training_plan;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import springweb.training_manager.models.entities.Weekdays;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrainingPlansWrite {
    @NotNull
    @Valid
    private Map<Weekdays, TrainingPlanWrite> planWriteMap = new HashMap<>();

    public void add(Weekdays weekday, int trainingId, String trainingTime) {
        var trainingPlan = new TrainingPlanWrite(trainingId, trainingTime);
        planWriteMap.put(weekday, trainingPlan);
    }

    public void add(Weekdays weekday) {
        var trainingPlan = new TrainingPlanWrite(0, null);
        planWriteMap.put(weekday, trainingPlan);
    }
}
