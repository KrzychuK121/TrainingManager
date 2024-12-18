package springweb.training_manager.services.WorkoutAssistantServices.WorkoutAssistantTypedServices;

import org.springframework.stereotype.Service;
import springweb.training_manager.models.entities.BodyPart;
import springweb.training_manager.models.entities.Training;
import springweb.training_manager.models.entities.User;
import springweb.training_manager.models.viewmodels.workout_assistant.WorkoutAssistantWrite;
import springweb.training_manager.repositories.for_controllers.TrainingRepository;

import java.util.List;
import java.util.Map;

@Service
public class WAMuscleGrowService extends WATypedBase {

    public WAMuscleGrowService(TrainingRepository trainingRepository) {
        super(trainingRepository);
    }

    @Override
    public Map<BodyPart, List<Training>> getTrainingsByBodyParts(
        List<BodyPart> bodyParts,
        User loggedUser
    ) {
        return getTrainingsForBodyParts(
            bodyParts,
            3,
            loggedUser
        );
    }

    @Override
    Training prepTrainingForGoal(WorkoutAssistantWrite data, Training toPrepare) {
        return null;
    }
}
