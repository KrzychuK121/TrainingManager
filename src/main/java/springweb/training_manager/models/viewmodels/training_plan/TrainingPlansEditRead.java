package springweb.training_manager.models.viewmodels.training_plan;

import lombok.AllArgsConstructor;
import lombok.Getter;
import springweb.training_manager.models.entities.TrainingPlan;
import springweb.training_manager.models.entities.Weekdays;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public class TrainingPlansEditRead {
    private Map<Weekdays, TrainingPlanEditRead> planEditReadMap;

    private static Map<Weekdays, TrainingPlanEditRead> plansListToMap(
        List<TrainingPlan> plans
    ) {
        return plans.stream()
             .collect(
                 Collectors.toMap(
                     plan -> plan.getTrainingSchedule()
                                 .getWeekday(),
                     TrainingPlanEditRead::new
                 )
             );
    }

    public TrainingPlansEditRead(List<TrainingPlan> plans) {
        this(
            plansListToMap(plans)
        );
    }
}
