package springweb.trainingmanager.repositories.forcontrollers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import springweb.trainingmanager.models.entities.Exercise;

import java.util.List;
import java.util.Optional;

public interface ExerciseRepository {
    List<Exercise> findAll();

    Exercise save(Exercise entity);

    Optional<Exercise> findById(Integer integer);

    boolean existsById(Integer integer);

    Page<Exercise> findAll(Pageable pageable);

    Optional<Exercise> findByExercise(Exercise exercise);
}
