package springweb.trainingmanager.repositories.beans;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import springweb.trainingmanager.models.entities.TrainingSchedule;
import springweb.trainingmanager.models.entities.TrainingScheduleId;
import springweb.trainingmanager.repositories.forcontrollers.TrainingScheduleRepository;

@Repository
interface SqlTrainingScheduleRepository
    extends TrainingScheduleRepository,
            JpaRepository<TrainingSchedule, TrainingScheduleId> {

}
