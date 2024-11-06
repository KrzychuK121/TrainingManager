package springweb.training_manager.repositories.for_controllers;

import springweb.training_manager.models.entities.ExerciseParameters;

import java.util.Optional;

public interface ExerciseParametersRepository {
    Optional<ExerciseParameters> findDuplication(ExerciseParameters entity);
}
