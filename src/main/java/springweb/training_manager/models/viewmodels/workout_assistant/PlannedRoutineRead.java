package springweb.training_manager.models.viewmodels.workout_assistant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import springweb.training_manager.models.entities.TrainingPlan;
import springweb.training_manager.models.entities.Weekdays;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public class PlannedRoutineRead {
    private Map<Weekdays, PlannedTrainingRead> schedules;

    public PlannedRoutineRead(List<TrainingPlan> plans) {
        schedules = plans.stream()
            .collect(
                Collectors.toMap(
                    plan -> plan.getTrainingSchedule()
                        .getWeekday(),
                    plan -> new PlannedTrainingRead(
                        plan.getTrainingSchedule()
                            .getTraining()
                    )
                )
            );
    }
}
