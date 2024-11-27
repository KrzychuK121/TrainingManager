package springweb.training_manager.repositories.for_controllers;

import springweb.training_manager.models.entities.DoneTraining;

import java.time.LocalDate;
import java.util.Optional;

public interface DoneTrainingRepository {
    Optional<DoneTraining> findExistingRegister(
        int routineId,
        int trainingId,
        LocalDate startDate
    );

    DoneTraining save(DoneTraining entity);
}
