package springweb.training_manager.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import springweb.training_manager.models.entities.DoneTraining;
import springweb.training_manager.models.entities.Training;
import springweb.training_manager.models.entities.TrainingRoutine;
import springweb.training_manager.models.entities.User;
import springweb.training_manager.models.viewmodels.done_training.DoneTrainingWrite;
import springweb.training_manager.repositories.for_controllers.DoneTrainingRepository;
import springweb.training_manager.repositories.for_controllers.TrainingRoutineRepository;

@RequiredArgsConstructor
@Service
@Slf4j
public class DoneTrainingService {
    private final DoneTrainingRepository doneTrainingsRepository;
    private final TrainingRoutineRepository trainingRoutineRepository;
    private final TrainingService trainingService;
    private final DoneExerciseService doneExerciseService;

    private DoneTraining toEntity(
        DoneTrainingWrite doneTrainingWrite,
        User loggedUser
    ) {
        var doneTraining = new DoneTraining();

        doneTraining.setId(doneTrainingWrite.getId());
        doneTraining.setEndDate(doneTrainingWrite.getEndDate());

        Training foundTraining = trainingService.getByIdForUse(
            doneTrainingWrite.getTrainingId(),
            loggedUser
        );
        // TODO: Maybe use service instead of repo?
        TrainingRoutine foundRoutine = trainingRoutineRepository.findById(
                doneTrainingWrite.getRoutineId()
            )
            .orElseThrow(() -> new IllegalArgumentException("Routine for DoneTraining not found"));
        doneTraining.setTraining(foundTraining);
        doneTraining.setRoutine(foundRoutine);

        return doneTraining;
    }

    @Transactional
    public void create(
        DoneTrainingWrite doneTraining,
        User loggedUser
    ) {
        var entityToSave = toEntity(doneTraining, loggedUser);
        var saved = doneTrainingsRepository.save(entityToSave);
        doneExerciseService.createAllForTrainingRegister(
            doneTraining.getDoneExercises(),
            saved
        );
    }
}
