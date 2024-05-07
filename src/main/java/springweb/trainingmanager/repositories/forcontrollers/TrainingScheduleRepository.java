package springweb.trainingmanager.repositories.forcontrollers;

import springweb.trainingmanager.models.entities.TrainingSchedule;
import springweb.trainingmanager.models.entities.TrainingScheduleId;

import java.util.List;
import java.util.Optional;

public interface TrainingScheduleRepository {
    List<TrainingSchedule> findAll();
    TrainingSchedule  save(TrainingSchedule entity);
    Optional<TrainingSchedule> findById(TrainingScheduleId trainingScheduleId);
    boolean existsById(TrainingScheduleId trainingScheduleId);
    void delete(TrainingSchedule entity);
}
