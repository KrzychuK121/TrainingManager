package springweb.training_manager.repositories.beans;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import springweb.training_manager.models.entities.Exercise;
import springweb.training_manager.repositories.for_controllers.ExerciseRepository;

import java.util.List;
import java.util.Optional;

//@RepositoryRestResource(path = "exercise")
@Repository
interface SqlExerciseRepository extends ExerciseRepository, JpaRepository<Exercise, Integer> {
    @Override
    @Query("SELECT e FROM Exercise e LEFT JOIN FETCH e.trainings")
    List<Exercise> findAll();

    @Override
    @Query(
        """
        SELECT e FROM Exercise e LEFT JOIN FETCH e.trainings 
        WHERE e.name = :#{#exercise.name} AND 
        e.description = :#{#exercise.description} AND 
        e.rounds = :#{#exercise.rounds} AND
        e.repetition = :#{#exercise.repetition} AND 
        e.time = :#{#exercise.time} AND 
        e.bodyPart = :#{#exercise.bodyPart} AND 
        e.weights = :#{#exercise.weights} AND 
        e.difficulty = :#{#exercise.difficulty}
        """
    )
    Optional<Exercise> findByExercise(@Param("exercise") Exercise exercise);
}
