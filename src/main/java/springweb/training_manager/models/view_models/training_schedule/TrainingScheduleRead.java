package springweb.training_manager.models.view_models.training_schedule;

import lombok.AllArgsConstructor;
import lombok.Getter;
import springweb.training_manager.models.entities.TrainingSchedule;

@Getter
@AllArgsConstructor
public class TrainingScheduleRead {
    private final String trainingTitle;
    private final String trainingDescription;

    public TrainingScheduleRead(final TrainingSchedule schedule) {
        this(
            schedule.getTraining()
                .getTitle(),
            schedule.getTraining()
                .getDescription()
        );
    }
}
