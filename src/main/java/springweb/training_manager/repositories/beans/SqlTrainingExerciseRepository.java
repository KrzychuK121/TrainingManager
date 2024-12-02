package springweb.training_manager.repositories.beans;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import springweb.training_manager.models.entities.TrainingExercise;
import springweb.training_manager.repositories.for_controllers.TrainingExerciseRepository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SqlTrainingExerciseRepository extends TrainingExerciseRepository,
    JpaRepository<TrainingExercise, Integer> {

    @Override
    Optional<TrainingExercise> findById(int id);

    @Override
    Optional<List<TrainingExercise>> findByTrainingId(int trainingId);

    @Override
    Optional<List<TrainingExercise>> findByExerciseId(int trainingExerciseId);

    @Override
    Optional<TrainingExercise> findByTrainingIdAndExerciseId(
        int trainingId,
        int exerciseId
    );

    @Override
    boolean existsByTrainingIdAndExerciseOwnerIsNotNull(int trainingId);

    @Modifying
    @Query("""
            DELETE FROM TrainingExercise t 
            WHERE t.exercise.id = :exerciseId 
                AND t.training.id NOT IN :ids
        """)
    @Override
    void deleteIfTrainingsNotIn(
        @Param("exerciseId") Integer exerciseId,
        @Param("ids") List<Integer> trainingIds
    );

    @Modifying
    @Query("""
            DELETE FROM TrainingExercise t 
            WHERE t.training.id = :trainingId 
                AND t.exercise.id NOT IN :ids
        """)
    @Override
    void deleteIfExercisesNotIn(
        @Param("trainingId") Integer trainingId,
        @Param("ids") List<Integer> exercisesIds
    );

    @Modifying
    @Override
    void deleteByTrainingId(int trainingId);

    @Modifying
    @Override
    void deleteByExerciseId(int exerciseId);
}
