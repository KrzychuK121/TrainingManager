package springweb.training_manager.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import springweb.training_manager.models.entities.ExerciseParameters;
import springweb.training_manager.models.viewmodels.exercise_parameters.ExerciseParametersRead;
import springweb.training_manager.models.viewmodels.exercise_parameters.ExerciseParametersWrite;
import springweb.training_manager.repositories.for_controllers.ExerciseParametersRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class ExerciseParametersService {
    private final ExerciseParametersRepository repository;

    public ExerciseParameters prepExerciseParameters(
        ExerciseParameters parametersWrite
    ) {
        var preparedParameters = NoDuplicationService.prepEntity(
            parametersWrite,
            repository,
            repository::save
        );
        if (preparedParameters == null) {
            log.info("Exercises must contain its parameters");
            throw new IllegalArgumentException("Exercises must contain its parameters");
        }
        return preparedParameters;
    }

    public ExerciseParameters prepExerciseParameters(
        ExerciseParametersWrite parametersWrite
    ) {
        return prepExerciseParameters(parametersWrite.toEntity());
    }

    public boolean parametersAreOrphaned(ExerciseParametersRead parametersRead) {
        return !repository.referencedInExercise(parametersRead.getId()) &&
            !repository.referencedInTrainingExercise(parametersRead.getId());
    }

    public void deleteIfOrphaned(ExerciseParametersRead parametersRead) {
        if (!parametersAreOrphaned(parametersRead))
            return;
        repository.deleteById(parametersRead.getId());
    }

    public void deleteIfOrphaned(List<ExerciseParametersRead> parametersRead) {
        var toDelete = parametersRead.stream()
            .filter(this::parametersAreOrphaned)
            .map(ExerciseParametersRead::getId)
            .toList();
        if (toDelete.isEmpty())
            return;
        repository.deleteAllById(toDelete);
    }
}
