package springweb.training_manager.repositories.for_controllers;

import springweb.training_manager.models.entities.DoneTraining;

public interface DoneTrainingRepository {
    DoneTraining save(DoneTraining entity);
}
