package springweb.trainingmanager.repositories.beans;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import springweb.trainingmanager.models.entities.TrainingRoutine;
import springweb.trainingmanager.repositories.forcontrollers.TrainingRoutineRepository;

import java.util.List;
import java.util.Optional;

@Repository
interface SqlTrainingRoutineRepository
    extends TrainingRoutineRepository,
            JpaRepository<TrainingRoutine, Integer> {

    @Override
    public Optional<TrainingRoutine> findByOwnerIdAndActiveTrue(String ownerId);
}
