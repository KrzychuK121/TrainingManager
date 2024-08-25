package springweb.training_manager.repositories.for_controllers;

import springweb.training_manager.models.entities.TrainingRoutine;

import java.util.List;
import java.util.Optional;

public interface TrainingRoutineRepository {
    List<TrainingRoutine> findAll();
    TrainingRoutine save(TrainingRoutine entity);
    Optional<TrainingRoutine> findById(Integer integer);
    boolean existsById(Integer integer);
    void delete(TrainingRoutine entity);
    Optional<TrainingRoutine> findByOwnerIdAndActiveTrue(String ownerId);
}
