package springweb.training_manager.repositories.beans;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import springweb.training_manager.models.entities.DoneExercise;
import springweb.training_manager.repositories.for_controllers.DoneExerciseRepository;

import java.util.Optional;

@Repository
interface SqlDoneExerciseRepository extends
    JpaRepository<DoneExercise, Integer>,
    DoneExerciseRepository {

    @Query("""
            SELECT DISTINCT de 
            FROM DoneExercise de
            WHERE de.trainingExercise.id = :#{#entity.trainingExercise.id}
                AND de.doneSeries = :#{#entity.doneSeries}
                AND de.doneTraining.id = :#{#entity.doneTraining.id}
        """)
    @Override
    Optional<DoneExercise> findDuplication(DoneExercise entity);
}
