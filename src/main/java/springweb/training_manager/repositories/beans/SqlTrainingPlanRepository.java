package springweb.training_manager.repositories.beans;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import springweb.training_manager.models.entities.TrainingPlan;
import springweb.training_manager.models.entities.User;
import springweb.training_manager.models.schemas.TrainingPlanId;
import springweb.training_manager.repositories.for_controllers.TrainingPlanRepository;

import java.util.List;
import java.util.Optional;

@Repository
interface SqlTrainingPlanRepository
    extends TrainingPlanRepository,
            JpaRepository<TrainingPlan, TrainingPlanId> {

    @Override
    Optional<List<TrainingPlan>> findByTrainingRoutineId(int trainingRoutineId);
    @Override
    Optional<List<TrainingPlan>> findByTrainingRoutineOwner(User owner);
    @Override
    Optional<List<TrainingPlan>> findByTrainingRoutineOwnerIdAndTrainingRoutineId(
        String userId,
        int routineId
    );
}
