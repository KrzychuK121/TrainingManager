package springweb.training_manager.repositories.for_controllers;

import springweb.training_manager.models.entities.TrainingPlan;
import springweb.training_manager.models.entities.User;
import springweb.training_manager.models.schemas.TrainingPlanId;

import java.util.List;
import java.util.Optional;

public interface TrainingPlanRepository {
    List<TrainingPlan> findAll();

    TrainingPlan save(TrainingPlan entity);

    Optional<TrainingPlan> findById(TrainingPlanId trainingPlanId);

    Optional<List<TrainingPlan>> findByTrainingRoutineId(int trainingRoutineId);

    Optional<List<TrainingPlan>> findByTrainingRoutineOwner(User owner);

    Optional<List<TrainingPlan>> findByTrainingRoutineOwnerIdAndTrainingRoutineId(
        String userId,
        int routineId
    );

    int countByTrainingScheduleId(int id);

    void delete (TrainingPlan entity);
}
