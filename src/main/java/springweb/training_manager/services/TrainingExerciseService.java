package springweb.training_manager.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import springweb.training_manager.models.entities.Exercise;
import springweb.training_manager.models.entities.ExerciseParameters;
import springweb.training_manager.models.entities.Training;
import springweb.training_manager.models.entities.TrainingExercise;
import springweb.training_manager.repositories.for_controllers.TrainingExerciseRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j
public class TrainingExerciseService {
    private final TrainingExerciseRepository repository;

    public TrainingExercise getByIdForUse(int id) {
        return repository.findById(id)
            .orElseThrow(
                () -> new IllegalArgumentException("TrainingExercise with provided id does not exist")
            );
    }

    public boolean trainingContainsPrivateExercises(int id) {
        return repository.existsByTrainingIdAndExerciseOwnerIsNotNull(id);
    }

    public List<TrainingExercise> updateTrainingExerciseConnection(
        Training training,
        Map<Exercise, ExerciseParameters> exercises
    ) {
        repository.deleteByTrainingId(training.getId());

        // Add missing connections and return them
        return exercises.entrySet()
            .stream()
            .map(
                entry -> {
                    var exercise = entry.getKey();
                    var parameters = entry.getValue();

                    return repository.save(
                        new TrainingExercise(
                            training,
                            exercise,
                            parameters
                        )
                    );
                }
            )
            .toList();
    }

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

        repository.deleteIfTrainingsNotIn(exercise.getId(), trainingIds);

        // Add missing connections and return them
        return trainings.stream()
            .map(
                training -> updateTrainingExerciseConnection(
                    training,
                    exercise,
                    exercise.getParameters()
                )
            )
            .toList();
    }

    private TrainingExercise updateTrainingExerciseConnection(
        Training training,
        Exercise exercise,
        ExerciseParameters parameters
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
                    parameters
                )
            ));
    }

    public void deleteByTrainingId(Training training) {
        repository.deleteByTrainingId(training.getId());
    }

    public void deleteByExerciseId(Exercise exercise) {
        repository.deleteByExerciseId(exercise.getId());
    }
}
