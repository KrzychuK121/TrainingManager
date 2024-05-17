package springweb.trainingmanager.repositories.beans;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import springweb.trainingmanager.models.entities.Exercise;
import springweb.trainingmanager.models.entities.TrainingSchedule;
import springweb.trainingmanager.repositories.forcontrollers.TrainingScheduleRepository;

import java.util.Optional;

@Repository
interface SqlTrainingScheduleRepository
    extends TrainingScheduleRepository,
            JpaRepository<TrainingSchedule, Integer> {
    @Override
    @Query(
        """
        SELECT e FROM TrainingSchedule e
        WHERE e.trainingId = :#{#entity.trainingId} AND 
        e.weekday = :#{#entity.weekday}
        """
    )
    Optional<TrainingSchedule> findDuplication(@Param("entity") TrainingSchedule entity);
}
