package springweb.training_manager.repositories.beans;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import springweb.training_manager.models.entities.DoneExercise;
import springweb.training_manager.repositories.for_controllers.DoneExerciseRepository;

@Repository
interface SqlDoneExerciseRepository extends
    JpaRepository<DoneExercise, Integer>,
    DoneExerciseRepository {

}
