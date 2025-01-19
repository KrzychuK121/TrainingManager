package springweb.training_manager.repositories.for_controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import springweb.training_manager.models.entities.Exercise;
import springweb.training_manager.models.viewmodels.exercise.ExerciseRead;

import java.util.List;
import java.util.Optional;

public interface ExerciseRepository extends DuplicationRepository<Exercise> {
    List<Exercise> findAll();

    Exercise save(Exercise entity);

    Optional<Exercise> findById(Integer integer);

    Optional<Exercise> findByIdAndOwnerId(int id, String ownerId);

    List<Exercise> findPublicOrOwnedBy(@Param("ownerId") String ownerId);

    Page<ExerciseRead> findPublicOrOwnedBy(
        @Param("ownerId") String ownerId,
        Pageable pageable
    );

    Page<ExerciseRead> findPublicOrOwnedByAndName(
        String ownerId,
        String name,
        Pageable pageable
    );

    Page<ExerciseRead> findPagedByName(
        String name,
        Pageable pageable
    );

    Page<ExerciseRead> findAllRead(Pageable pageable);

    @Override
    Optional<Exercise> findDuplication(Exercise exercise);

    boolean existsById(Integer integer);

    void deleteById(Integer integer);

    void delete(Exercise entity);
}
