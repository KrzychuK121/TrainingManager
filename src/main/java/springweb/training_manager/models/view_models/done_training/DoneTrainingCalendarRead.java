package springweb.training_manager.models.view_models.done_training;

import lombok.Getter;
import springweb.training_manager.models.entities.DoneTraining;

import java.time.LocalDateTime;

@Getter
public class DoneTrainingCalendarRead {
    private final int id;
    private final String title;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final boolean done;

    public DoneTrainingCalendarRead(
        DoneTraining doneTraining
    ) {
        id = doneTraining.getId();
        title = doneTraining.getTraining()
            .getTitle();
        startDate = doneTraining.getStartDate();
        endDate = doneTraining.getEndDate();
        done = doneTraining.getEndDate() != null;
    }
}
