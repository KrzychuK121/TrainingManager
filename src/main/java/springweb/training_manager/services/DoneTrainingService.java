package springweb.training_manager.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import springweb.training_manager.models.entities.DoneTraining;
import springweb.training_manager.repositories.for_controllers.DoneExerciseRepository;
import springweb.training_manager.repositories.for_controllers.DoneTrainingRepository;

@RequiredArgsConstructor
@Service
@Slf4j
public class DoneTrainingService {
    private final DoneTrainingRepository doneTrainingsRepo;
    private final DoneExerciseRepository doneExercisesRepo;

    @Transactional
    public void create(DoneTraining doneTrainings) {
        // TODO: First save DoneTrainings and then save all the done exercises
        
    }
}
