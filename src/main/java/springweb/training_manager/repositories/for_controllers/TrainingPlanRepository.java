package springweb.training_manager.repositories.for_controllers;

import org.springframework.data.repository.query.Param;
import springweb.training_manager.models.composite_ids.TrainingPlanId;
import springweb.training_manager.models.entities.TrainingPlan;
import springweb.training_manager.models.entities.User;
import springweb.training_manager.models.entities.Weekdays;

import java.time.LocalTime;
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

    List<TrainingPlan> findAllActivePlansForWeekdayAndTrainingTimeBetween(
        Weekdays weekday,
        @Param("startTime") LocalTime startTime,
        @Param("endTime") LocalTime endTime
    );

    int countByTrainingScheduleId(int id);

    void delete(TrainingPlan entity);
}
