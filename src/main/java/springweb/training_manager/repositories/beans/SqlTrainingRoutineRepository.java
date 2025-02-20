package springweb.training_manager.repositories.beans;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;
import springweb.training_manager.models.entities.TrainingRoutine;
import springweb.training_manager.models.entities.User;
import springweb.training_manager.repositories.for_controllers.TrainingRoutineRepository;

import java.util.Optional;

@Repository
interface SqlTrainingRoutineRepository
    extends TrainingRoutineRepository,
    JpaRepository<TrainingRoutine, Integer> {

    @Override
    Optional<TrainingRoutine> findByOwnerIdAndActiveTrue(String ownerId);

    @Override
    Page<TrainingRoutine> findAll(Pageable pageable);

    @Override
    Page<TrainingRoutine> findByOwner(
        User owner,
        Pageable page
    );

    @Override
    boolean existsByIdAndOwnerId(int id, String ownerId);

    @Override
    boolean existsByIdAndOwnerIdAndActiveIsFalse(int id, String ownerId);

    @Override
    @Procedure("switch_active_procedure")
    void switchActive(Integer id, String providedOwnerId);

    @Override
    @Procedure("delete_training_routine")
    void deleteTR(Integer id);
}
