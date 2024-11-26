package springweb.training_manager.repositories.for_controllers;

import springweb.training_manager.models.entities.DoneExercise;

import java.util.List;
import java.util.Optional;

public interface DoneExerciseRepository extends DuplicationRepository<DoneExercise> {
    DoneExercise save(DoneExercise entity);

    <S extends DoneExercise> List<S> saveAll(Iterable<S> entities);

    @Override
    Optional<DoneExercise> findDuplication(DoneExercise entity);
}
