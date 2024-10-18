package springweb.training_manager.repositories.for_controllers;

import springweb.training_manager.models.entities.TrainingSchedule;

import java.util.List;
import java.util.Optional;

public interface TrainingScheduleRepository extends DuplicationRepository<TrainingSchedule> {
    List<TrainingSchedule> findAll();

    TrainingSchedule save(TrainingSchedule entity);

    Optional<TrainingSchedule> findById(Integer id);

    boolean existsById(Integer id);

    @Override
    Optional<TrainingSchedule> findDuplication(TrainingSchedule entity);
}
