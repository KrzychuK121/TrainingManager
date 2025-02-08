package springweb.training_manager.models.view_models.training_plan;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;
import springweb.training_manager.models.view_models.enums.WeekdayRead;
import springweb.training_manager.models.view_models.training_routine.TrainingRoutineReadIndex;

import java.util.List;

@AllArgsConstructor
@Getter
public class TrainingPlansPagedRead {
    private Page<TrainingRoutineReadIndex> plans;
    // Important! This has to be the list due to need to save week days order.
    private final List<WeekdayRead> weekdays = WeekdayRead.getWeekdaysRead();
}
