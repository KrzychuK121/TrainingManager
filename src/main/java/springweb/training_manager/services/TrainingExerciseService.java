package springweb.training_manager.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import springweb.training_manager.exceptions.NotOwnedByUserException;
import springweb.training_manager.models.entities.*;
import springweb.training_manager.repositories.for_controllers.TrainingExerciseRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j
public class TrainingExerciseService {
    private final TrainingExerciseRepository repository;

    public TrainingExercise getByTrainingIdAndExerciseIdForUse(
        int trainingId,
        int exerciseId,
        User loggedUser
    ) {
        var found = repository.findByTrainingIdAndExerciseId(trainingId, exerciseId)
            .orElseThrow(
                () -> new IllegalArgumentException("TrainingExercise with provided id does not exist")
            );

        if (
            !UserService.isPermittedToReadFor(
                loggedUser,
                found.getExercise()
                    .getOwner()
            )
                || !UserService.isPermittedToReadFor(
                loggedUser,
                found.getTraining()
                    .getOwner()
            )
        )
            throw new NotOwnedByUserException(
                "User can't access for use training or exercise in TrainingExercise relationship."
            );

        return found;
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
