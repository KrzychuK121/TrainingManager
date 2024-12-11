package springweb.training_manager.models.viewmodels.workout_assistant;

import lombok.Getter;
import springweb.training_manager.models.entities.Training;
import springweb.training_manager.models.viewmodels.training.TrainingRead;
import springweb.training_manager.services.TrainingService;

@Getter
public class PlannedTrainingRead {
    private final TrainingRead training;
    private final int totalBurnedKcal;

    public PlannedTrainingRead(Training training) {
        var trainingRead = new TrainingRead(training);
        this.training = trainingRead;
        totalBurnedKcal = TrainingService.getTotalBurnedKcal(trainingRead);
    }
}
