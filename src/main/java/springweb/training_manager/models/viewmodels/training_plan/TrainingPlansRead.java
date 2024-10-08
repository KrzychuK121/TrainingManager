package springweb.training_manager.models.viewmodels.training_plan;

import lombok.AllArgsConstructor;
import lombok.Getter;
import springweb.training_manager.models.entities.Weekdays;
import springweb.training_manager.models.viewmodels.enums.WeekdayRead;
import springweb.training_manager.models.viewmodels.training_routine.TrainingRoutineReadIndex;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public class TrainingPlansRead {
    private List<TrainingRoutineReadIndex> plans;
    // Important! This has to be the list due to need to save week days order.
    private final List<WeekdayRead> weekdays = Arrays.stream(Weekdays.values())
        .map(
            WeekdayRead::new
        ).toList();
}
