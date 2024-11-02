package springweb.training_manager.models.viewmodels.training_plan;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalTime;

@AllArgsConstructor
@Getter
public class TrainingReminderRead {
    private String reminderTitle;
    private String trainingTitle;
    private LocalTime time;
}
