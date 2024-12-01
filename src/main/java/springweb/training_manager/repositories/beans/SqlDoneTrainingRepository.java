package springweb.training_manager.repositories.beans;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import springweb.training_manager.models.entities.DoneTraining;
import springweb.training_manager.repositories.for_controllers.DoneTrainingRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
interface SqlDoneTrainingRepository extends
    JpaRepository<DoneTraining, Integer>,
    DoneTrainingRepository {

    @Override
    @Query("""
            SELECT dt FROM DoneTraining dt
            WHERE dt.training.id = :trainingId
                AND dt.routine.id = :routineId
                AND FUNCTION('DATE', dt.startDate) = :startDate 
        """)
    Optional<DoneTraining> findExistingRegister(
        @Param("routineId") int routineId,
        @Param("trainingId") int trainingId,
        @Param("startDate") LocalDate startDate
    );

    @Override
    Optional<DoneTraining> findByIdAndRoutineOwnerId(
        @Param("id") int id,
        @Param("ownerId") String ownerId
    );

    @Override
    List<DoneTraining> findAllByRoutineOwnerId(@Param("ownerId") String ownerId);
}
