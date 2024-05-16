package springweb.trainingmanager.repositories.beans;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import springweb.trainingmanager.models.entities.TrainingSchedule;
import springweb.trainingmanager.repositories.forcontrollers.TrainingScheduleRepository;

import java.util.Optional;

@Repository
interface SqlTrainingScheduleRepository
    extends TrainingScheduleRepository,
            JpaRepository<TrainingSchedule, Integer> {
    // TODO: Create "implementation" (using annotations) to find same entity as provided.
    @Override
    Optional<TrainingSchedule> findDuplication(TrainingSchedule entity);
}
