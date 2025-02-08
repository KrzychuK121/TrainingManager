package springweb.training_manager.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import springweb.training_manager.models.entities.*;
import springweb.training_manager.models.view_models.done_training.DoneTrainingCalendarRead;
import springweb.training_manager.models.view_models.done_training.DoneTrainingDetailsRead;
import springweb.training_manager.models.view_models.done_training.DoneTrainingWrite;
import springweb.training_manager.repositories.for_controllers.DoneTrainingRepository;
import springweb.training_manager.repositories.for_controllers.TrainingRoutineRepository;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class DoneTrainingService {
    private final DoneTrainingRepository repository;
    private final TrainingRoutineRepository trainingRoutineRepository;
    private final TrainingService trainingService;
    private final DoneExerciseService doneExerciseService;

    private DoneTraining toEntity(
        DoneTrainingWrite doneTrainingWrite,
        User loggedUser
    ) {
        var doneTraining = new DoneTraining();

        doneTraining.setStartDate(doneTrainingWrite.getStartDate());
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

    public List<DoneTrainingCalendarRead> getAllByUser(User loggedUser) {
        return repository.findAllByRoutineOwnerId(loggedUser.getId())
            .stream()
            .map(DoneTrainingCalendarRead::new)
            .toList();
    }

    public DoneTrainingDetailsRead getById(
        int id,
        User loggedUser
    ) {
        var found = repository.findByIdAndRoutineOwnerId(
                id,
                loggedUser.getId()
            )
            .orElse(null);
        return found != null
            ? new DoneTrainingDetailsRead(found)
            : null;
    }

    private DoneTraining getDoneTrainingBy(
        DoneTrainingWrite doneTrainingWrite,
        LocalDate date
    ) {
        return repository.findExistingRegister(
                doneTrainingWrite.getRoutineId(),
                doneTrainingWrite.getTrainingId(),
                date
            )
            .orElseThrow(
                () -> new IllegalArgumentException(
                    "Could not find DoneTraining for provided routineId, trainingId and date"
                )
            );
    }

    private void create(
        DoneTrainingWrite doneTrainingWrite,
        User loggedUser
    ) {
        var entityToSave = toEntity(doneTrainingWrite, loggedUser);
        var saved = repository.save(entityToSave);
        doneExerciseService.createAllForTrainingRegister(
            doneTrainingWrite.getDoneExercises(),
            saved,
            loggedUser
        );
    }

    @Transactional
    public void createIfNotExist(
        DoneTrainingWrite doneTrainingWrite,
        User loggedUser
    ) {
        var nowDate = LocalDate.now();
        try {
            getDoneTrainingBy(doneTrainingWrite, nowDate);
        } catch (IllegalArgumentException ex) {
            create(doneTrainingWrite, loggedUser);
        }
    }

    @Transactional
    public void edit(
        DoneTraining toEdit,
        DoneTrainingWrite doneTrainingWrite,
        User loggedUser
    ) {
        if (
            toEdit.getDoneExercises() != null
                && !toEdit.getDoneExercises()
                .isEmpty()
        )
            throw new IllegalStateException(
                "You can't edit existing done training register " +
                    "because it contains associated done exercises"
            );
        if (
            toEdit.getEndDate() != null
                && !UserService.userIsInRole(loggedUser, Role.ADMIN)
        )
            throw new IllegalStateException(
                "You can't edit existing done training register " +
                    "because this training was finished and you have no permissions to do that."
            );
        var entityToEdit = toEntity(doneTrainingWrite, loggedUser);
        entityToEdit.setId(toEdit.getId());
        repository.save(entityToEdit);

        doneExerciseService.createAllForTrainingRegister(
            doneTrainingWrite.getDoneExercises(),
            entityToEdit,
            loggedUser
        );
    }

    @Transactional
    public void createOrEdit(
        DoneTrainingWrite doneTrainingWrite,
        User loggedUser
    ) {
        var nowDate = LocalDate.now();
        try {
            var foundRegister = getDoneTrainingBy(doneTrainingWrite, nowDate);

            edit(
                foundRegister,
                doneTrainingWrite,
                loggedUser
            );
        } catch (IllegalArgumentException ex) {
            create(doneTrainingWrite, loggedUser);
        }
    }
}
