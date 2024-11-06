package springweb.training_manager.repositories.beans;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import springweb.training_manager.models.entities.ExerciseParameters;
import springweb.training_manager.repositories.for_controllers.ExerciseParametersRepository;

import java.util.Optional;

@Repository
interface SqlExerciseParametersRepository
    extends ExerciseParametersRepository,
    JpaRepository<ExerciseParameters, Integer> {
    @Query("""
            SELECT ep
            FROM ExerciseParameters ep
            WHERE ep.rounds = :#{#entity.rounds}
                AND ep.repetition = :#{#entity.repetition}
                AND ep.time = :#{#entity.time}
                AND ep.weights = :#{#entity.weights}
                AND ep.difficulty = :#{#entity.difficulty}
        """)
    @Override
    Optional<ExerciseParameters> findDuplication(ExerciseParameters entity);
}
