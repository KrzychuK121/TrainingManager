package springweb.training_manager.repositories.for_controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import springweb.training_manager.models.entities.TrainingRoutine;
import springweb.training_manager.models.entities.User;

import java.util.List;
import java.util.Optional;

public interface TrainingRoutineRepository {
    List<TrainingRoutine> findAll();

    TrainingRoutine save(TrainingRoutine entity);

    Optional<TrainingRoutine> findById(Integer integer);

    Optional<TrainingRoutine> findByOwnerIdAndActiveTrue(String ownerId);

    Page<TrainingRoutine> findAll(Pageable pageable);

    Page<TrainingRoutine> findByOwner(
        User owner,
        Pageable page
    );

    boolean existsById(Integer integer);

    boolean existsByIdAndOwnerId(int id, String ownerId);

    boolean existsByIdAndOwnerIdAndActiveIsFalse(int id, String ownerId);

    /**
     * This method (stored procedure) is responsible for switching previous active routine
     * to new routine provided in parameter for specified user.
     *
     * @param id              routine to active
     * @param providedOwnerId owner of routine to active
     */
    void switchActive(Integer id, String providedOwnerId);

    void delete(TrainingRoutine entity);

    /**
     * This method (stored procedure) is responsible for deleting all training plans that
     * contains provided <code>id</code>, training routine row itself and all training
     * schedules which are no longer mentioned in training plan table (last row that used
     * the schedule was removed right now).
     *
     * @param id item to delete
     */
    void deleteTR(Integer id);
}
