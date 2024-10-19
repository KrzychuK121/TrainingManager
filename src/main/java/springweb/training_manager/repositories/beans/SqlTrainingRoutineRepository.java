package springweb.training_manager.repositories.beans;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;
import springweb.training_manager.models.entities.TrainingRoutine;
import springweb.training_manager.repositories.for_controllers.TrainingRoutineRepository;

import java.util.Optional;

@Repository
interface SqlTrainingRoutineRepository
    extends TrainingRoutineRepository,
    JpaRepository<TrainingRoutine, Integer> {

    @Override
    Optional<TrainingRoutine> findByOwnerIdAndActiveTrue(String ownerId);

    @Override
    boolean existsByIdAndOwnerId(int id, String ownerId);

    @Override
    @Procedure("delete_training_routine")
    void deleteTR(Integer id);
}
