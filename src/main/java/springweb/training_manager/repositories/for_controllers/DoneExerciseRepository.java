package springweb.training_manager.repositories.for_controllers;

import springweb.training_manager.models.entities.DoneExercise;

import java.util.List;

public interface DoneExerciseRepository {
    DoneExercise save(DoneExercise entity);

    <S extends DoneExercise> List<S> saveAll(Iterable<S> entities);
}
