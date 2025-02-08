package springweb.training_manager.models.view_models.training_plan;

import lombok.AllArgsConstructor;
import lombok.Getter;
import springweb.training_manager.models.entities.ReminderType;

import java.time.LocalTime;

@AllArgsConstructor
@Getter
public class TrainingReminderRead {
    private String reminderTitle;
    private String trainingTitle;
    private LocalTime time;
    private ReminderType type;

    public TrainingReminderRead(
        TrainingReminderRead toCopy,
        ReminderType type
    ) {
        this(
            toCopy.reminderTitle,
            toCopy.trainingTitle,
            toCopy.time,
            type
        );
    }
}
