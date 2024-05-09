package springweb.trainingmanager.repositories.forcontrollers;

import springweb.trainingmanager.models.entities.TrainingSchedule;

import java.util.List;
import java.util.Optional;

public interface TrainingScheduleRepository {
    List<TrainingSchedule> findAll();
    TrainingSchedule  save(TrainingSchedule entity);
    Optional<TrainingSchedule> findById(Integer id);
    boolean existsById(Integer id);
    void delete(TrainingSchedule entity);
}
