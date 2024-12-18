package springweb.training_manager.services.WorkoutAssistantServices.WorkoutAssistantTypedServices;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import springweb.training_manager.models.entities.BodyPart;
import springweb.training_manager.models.entities.Training;
import springweb.training_manager.models.entities.User;
import springweb.training_manager.models.viewmodels.workout_assistant.WorkoutAssistantWrite;
import springweb.training_manager.repositories.for_controllers.TrainingRepository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public abstract class WATypedBase {
    private final TrainingRepository trainingRepository;

    protected Map<BodyPart, List<Training>> getTrainingsForBodyParts(
        List<BodyPart> bodyParts,
        int bodyPartCount,
        User loggedUser
    ) {
        if (bodyParts == null || bodyParts.isEmpty())
            return Collections.emptyMap();
        var toReturn = new HashMap<BodyPart, List<Training>>();
        bodyParts.forEach(
            bodyPart -> {
                var trainings = trainingRepository.findForUseByMostBodyPart(
                    loggedUser.getId(),
                    bodyPart.name(),
                    bodyPartCount
                );

                toReturn.put(bodyPart, trainings);
            }
        );

        return toReturn;
    }

    public Map<BodyPart, List<Training>> prepTrainingsForUserGoal(
        WorkoutAssistantWrite workoutAssistantWrite,
        Map<BodyPart, List<Training>> bodyPartsTrainings
    ) {
        bodyPartsTrainings.replaceAll(
            (bodyPart, trainings) -> trainings.stream()
                .map(training -> prepTrainingForGoal(workoutAssistantWrite, training))
                .toList()
        );

        return bodyPartsTrainings;
    }

    public abstract Map<BodyPart, List<Training>> getTrainingsByBodyParts(
        List<BodyPart> bodyParts,
        User loggedUser
    );

    abstract Training prepTrainingForGoal(
        WorkoutAssistantWrite data,
        Training toPrepare
    );
}
