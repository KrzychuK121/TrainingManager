package springweb.trainingmanager.repositories.forcontrollers;

import springweb.trainingmanager.models.entities.TrainingRoutine;

import java.util.List;
import java.util.Optional;

public interface TrainingRoutineRepository {
    List<TrainingRoutine> findAll();
    TrainingRoutine save(TrainingRoutine entity);
    Optional<TrainingRoutine> findById(Integer integer);
    boolean existsById(Integer integer);
    void delete(TrainingRoutine entity);
}
