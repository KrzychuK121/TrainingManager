package springweb.training_manager.repositories.for_controllers;

import org.springframework.data.repository.query.Param;
import springweb.training_manager.models.entities.TrainingExercise;

import java.util.List;
import java.util.Optional;

public interface TrainingExerciseRepository {
    TrainingExercise save(TrainingExercise entity);

    Optional<List<TrainingExercise>> findByTrainingId(int trainingId);

    Optional<List<TrainingExercise>> findByExerciseId(int trainingExerciseId);

    Optional<TrainingExercise> findByTrainingIdAndExerciseId(
        int trainingId,
        int exerciseId
    );

    void deleteIfNotIn(
        @Param("exerciseId") Integer exerciseId,
        @Param("ids") List<Integer> trainingIds
    );

    void deleteByExerciseId(int exerciseId);
}
