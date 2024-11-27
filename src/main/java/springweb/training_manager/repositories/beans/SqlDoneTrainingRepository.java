package springweb.training_manager.repositories.beans;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import springweb.training_manager.models.composite_ids.DoneTrainingId;
import springweb.training_manager.models.entities.DoneTraining;
import springweb.training_manager.repositories.for_controllers.DoneTrainingRepository;

@Repository
interface SqlDoneTrainingRepository extends
    JpaRepository<DoneTraining, Integer>,
    DoneTrainingRepository {

}
