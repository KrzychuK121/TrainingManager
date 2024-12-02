package springweb.training_manager.repositories.beans;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import springweb.training_manager.models.composite_ids.TrainingPlanId;
import springweb.training_manager.models.entities.TrainingPlan;
import springweb.training_manager.models.entities.User;
import springweb.training_manager.models.entities.Weekdays;
import springweb.training_manager.repositories.for_controllers.TrainingPlanRepository;

import java.time.LocalTime;
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

    /**
     * This method retrieves all active routines for specified day of the week and that
     * time is between provided times.<br/><br/>
     * <strong>NOTE</strong>: Make sure to provide less <code>startTime</code> than
     * <code>endTime</code>. Otherwise, this method might not work as intended.
     *
     * @param weekday   day of the week for which we look for trainings to do
     * @param startTime from which training time we want to start looking
     * @param endTime   to which training time we want to stop looking
     *
     * @return All plans that are active, for provided day of week and between provided
     * times
     */
    @Override
    @Query("""
            SELECT p
            FROM TrainingPlan p
            WHERE p.trainingSchedule.weekday = :weekday
                AND p.trainingRoutine.active = true
                AND p.trainingTime BETWEEN :startTime AND :endTime
        """)
    List<TrainingPlan> findAllActivePlansForWeekdayAndTrainingTimeBetween(
        @Param("weekday") Weekdays weekday,
        @Param("startTime") LocalTime startTime,
        @Param("endTime") LocalTime endTime
    );

    @Override
    int countByTrainingScheduleId(int id);

    @Override
    void delete(TrainingPlan entity);

}
