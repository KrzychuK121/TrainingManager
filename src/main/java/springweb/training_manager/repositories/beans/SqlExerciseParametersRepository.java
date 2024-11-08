package springweb.training_manager.repositories.beans;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
                AND (
                    ep.time IS NULL AND :#{#entity.time} IS NULL 
                    OR ep.time = :#{#entity.time}
                )
                AND ep.weights = :#{#entity.weights}
        """)
    @Override
    Optional<ExerciseParameters> findDuplication(ExerciseParameters entity);

    @Query(value = """
            SELECT parameters_referenced_in('exercise', :parametersId)
        """,
        nativeQuery = true
    )
    @Override
    boolean referencedInExercise(@Param("parametersId") Integer parametersId);

    @Query(value = """
            SELECT parameters_referenced_in('training_exercise', :parametersId)
        """,
        nativeQuery = true
    )
    @Override
    boolean referencedInTrainingExercise(@Param("parametersId") Integer parametersId);

    @Override
    void deleteById(Integer integer);
}
