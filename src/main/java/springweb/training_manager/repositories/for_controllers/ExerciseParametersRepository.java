package springweb.training_manager.repositories.for_controllers;

import springweb.training_manager.models.entities.ExerciseParameters;

import java.util.Optional;

public interface ExerciseParametersRepository extends DuplicationRepository<ExerciseParameters> {
    Optional<ExerciseParameters> findDuplication(ExerciseParameters entity);

    ExerciseParameters save(ExerciseParameters entity);

    boolean referencedInExercise(Integer parametersId);

    boolean referencedInTrainingExercise(Integer parametersId);

    void deleteById(Integer integer);

    void deleteAllById(Iterable<? extends Integer> ids);
}
