package springweb.training_manager.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import springweb.training_manager.exceptions.NotOwnedByUserException;
import springweb.training_manager.models.entities.Role;
import springweb.training_manager.models.entities.TrainingRoutine;
import springweb.training_manager.models.entities.User;
import springweb.training_manager.models.view_models.training_routine.TrainingRoutineRead;
import springweb.training_manager.repositories.for_controllers.TrainingRoutineRepository;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class TrainingRoutineService {
    private final TrainingRoutineRepository repository;

    public List<TrainingRoutineRead> getAll() {
        return repository.findAll()
            .stream()
            .map(TrainingRoutineRead::new)
            .collect(Collectors.toList());
    }

    public boolean existsByIdAndOwnedBy(int id, String ownerId) {
        return repository.existsByIdAndOwnerId(id, ownerId);
    }

    public boolean existsAndIsNotActive(int id, String ownerId) {
        return repository.existsByIdAndOwnerIdAndActiveIsFalse(id, ownerId);
    }

    public TrainingRoutine getById(int id, String userId) {
        TrainingRoutine found = repository.findById(id)
            .orElseThrow(
                () -> new IllegalArgumentException(
                    "Nie znaleziono rutyny treningowej o podanym numerze id."
                )
            );

        if (userId == null)
            return found;

        var ownerId = found.getOwner()
            .getId();
        if (ownerId.equals(userId))
            return found;
        else
            throw new NotOwnedByUserException("Nie masz dostępu do tej rutyny.");
    }

    public TrainingRoutine getUserActiveRoutine(String ownerId) {
        return repository.findByOwnerIdAndActiveTrue(ownerId)
            .orElseThrow(() -> new IllegalStateException("Użytkownik nie posiada aktywnej rutyny treningowej."));
    }

    public TrainingRoutine createNew(TrainingRoutine toSave) {
        return repository.save(toSave);
    }

    public TrainingRoutine createNewByUser(User user) {
        var routine = new TrainingRoutine(user, List.of());
        return createNew(routine);
    }

    public void switchActive(
        int id,
        User loggedUser
    ) {
        if (UserService.userIsInRole(loggedUser, Role.ADMIN))
            return;

        if (
            !existsAndIsNotActive(
                id,
                loggedUser.getId()
            )
        )
            throw new IllegalArgumentException(
                "Provided user is not an owner of provided routine or routine you want to active is already activated"
            );
        repository.switchActive(id, loggedUser.getId());
    }

    public void delete(TrainingRoutine toDelete) {
        repository.delete(toDelete);
    }

    public void deleteById(int id, String ownerId) {
        if (!repository.existsById(id))
            throw new IllegalArgumentException(
                "Nie znaleziono rutyny treningowej o podanym numerze id."
            );

        if (ownerId == null || existsByIdAndOwnedBy(id, ownerId))
            repository.deleteTR(id);
    }
}
