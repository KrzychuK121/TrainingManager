package springweb.trainingmanager.models.viewmodels.trainingSchedule;

import springweb.trainingmanager.models.entities.TrainingSchedule;
import springweb.trainingmanager.models.entities.Weekdays;

public class TrainingScheduleRead {
    private final String trainingTitle;
    private final String trainingDescription;

    public TrainingScheduleRead(final TrainingSchedule schedule) {
        var training = schedule.getTraining();
        trainingTitle = training.getTitle();
        trainingDescription = training.getDescription();
    }

    public TrainingScheduleRead(
        final String trainingTitle,
        final String trainingDescription
    ) {
        this.trainingTitle = trainingTitle;
        this.trainingDescription = trainingDescription;
    }

    public String getTrainingTitle() {
        return trainingTitle;
    }
    public String getTrainingDescription() {
        return trainingDescription;
    }
}
