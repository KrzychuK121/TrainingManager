package springweb.trainingmanager.repositories.forcontrollers;

import springweb.trainingmanager.models.entities.TrainingPlan;
import springweb.trainingmanager.models.entities.User;
import springweb.trainingmanager.models.schemas.TrainingPlanId;

import java.util.List;
import java.util.Optional;

public interface TrainingPlanRepository {
    List<TrainingPlan> findAll();
    TrainingPlan save(TrainingPlan entity);
    Optional<TrainingPlan> findById(TrainingPlanId trainingPlanId);
    Optional<List<TrainingPlan>> findByTrainingRoutineId(int trainingRoutineId);
    Optional<List<TrainingPlan>> findByTrainingRoutineOwner(User owner);
    boolean existsById(TrainingPlanId trainingPlanId);
    void deleteById(TrainingPlanId trainingPlanId);
}
