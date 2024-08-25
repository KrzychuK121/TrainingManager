package springweb.training_manager.models.viewmodels.training_schedule;

import springweb.training_manager.models.entities.TrainingSchedule;

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
