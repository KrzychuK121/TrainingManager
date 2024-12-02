package springweb.training_manager.repositories.for_controllers;

import org.springframework.data.repository.query.Param;
import springweb.training_manager.models.entities.DoneTraining;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DoneTrainingRepository {
    Optional<DoneTraining> findExistingRegister(
        int routineId,
        int trainingId,
        LocalDate startDate
    );

    Optional<DoneTraining> findByIdAndRoutineOwnerId(
        @Param("id") int id,
        @Param("ownerId") String ownerId
    );

    List<DoneTraining> findAllByRoutineOwnerId(@Param("ownerId") String ownerId);

    boolean existsForOwnerForDate(
        @Param("ownerId") String ownerId,
        @Param("startDate") LocalDate startDate
    );

    DoneTraining save(DoneTraining entity);
}
