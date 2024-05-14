package springweb.trainingmanager.repositories.beans;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import springweb.trainingmanager.models.entities.TrainingPlan;
import springweb.trainingmanager.models.entities.TrainingRoutine;
import springweb.trainingmanager.models.schemas.TrainingPlanId;
import springweb.trainingmanager.repositories.forcontrollers.TrainingPlanRepository;

import java.util.List;
import java.util.Optional;

@Repository
interface SqlTrainingPlanRepository
    extends TrainingPlanRepository,
            JpaRepository<TrainingPlan, TrainingPlanId> {

    @Override
    Optional<List<TrainingPlan>> findByTrainingRoutineId(int trainingRoutineId);
}
