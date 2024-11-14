package springweb.training_manager.repositories.for_controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import springweb.training_manager.models.entities.Exercise;

import java.util.List;
import java.util.Optional;

public interface ExerciseRepository {
    List<Exercise> findAll();

    Exercise save(Exercise entity);

    Optional<Exercise> findById(Integer integer);

    Optional<Exercise> findByIdAndOwnerId(int id, String ownerId);

    boolean existsById(Integer integer);

    Page<Exercise> findAll(Pageable pageable);

    Optional<Exercise> findByExercise(Exercise exercise);

    void deleteById(Integer integer);

    void delete(Exercise entity);
}
