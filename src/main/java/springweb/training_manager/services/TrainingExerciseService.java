package springweb.training_manager.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import springweb.training_manager.models.entities.Exercise;
import springweb.training_manager.models.entities.Training;
import springweb.training_manager.models.entities.TrainingExercise;
import springweb.training_manager.repositories.for_controllers.TrainingExerciseRepository;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class TrainingExerciseService {
    private final TrainingExerciseRepository repository;

    public List<TrainingExercise> updateTrainingExerciseConnection(
        Exercise exercise,
        List<Training> trainings
    ) {
        // Delete connections not defined in update
        if (trainings == null || trainings.isEmpty()) {
            repository.deleteByExerciseId(exercise.getId());
            return new ArrayList<>();
        }
        
        var trainingIds = trainings.stream()
            .map(Training::getId)
            .toList();

        repository.deleteIfNotIn(exercise.getId(), trainingIds);

        // Add missing connections and return them
        return trainings.stream()
            .map(
                training -> updateTrainingExerciseConnection(exercise, training)
            )
            .toList();
    }

    private TrainingExercise updateTrainingExerciseConnection(
        Exercise exercise,
        Training training
    ) {
        var trainingId = training.getId();
        var exerciseId = exercise.getId();
        return repository.findByTrainingIdAndExerciseId(
                trainingId,
                exerciseId
            )
            .orElseGet(() -> repository.save(
                new TrainingExercise(
                    training,
                    exercise,
                    exercise.getParameters()
                )
            ));
    }
}
