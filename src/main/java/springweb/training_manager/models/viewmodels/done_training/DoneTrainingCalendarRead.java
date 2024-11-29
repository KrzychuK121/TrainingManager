package springweb.training_manager.models.viewmodels.done_training;

import lombok.Getter;
import springweb.training_manager.models.entities.DoneTraining;

import java.time.LocalDateTime;

@Getter
public class DoneTrainingCalendarRead {
    private final String title;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;

    public DoneTrainingCalendarRead(
        DoneTraining doneTraining
    ) {
        title = doneTraining.getTraining()
            .getTitle();
        startDate = doneTraining.getStartDate();
        endDate = doneTraining.getEndDate();
    }
}
