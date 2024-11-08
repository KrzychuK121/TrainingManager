package springweb.training_manager.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import springweb.training_manager.models.entities.ExerciseParameters;
import springweb.training_manager.models.viewmodels.exercise_parameters.ExerciseParametersRead;
import springweb.training_manager.models.viewmodels.exercise_parameters.ExerciseParametersWrite;
import springweb.training_manager.repositories.for_controllers.ExerciseParametersRepository;

@RequiredArgsConstructor
@Service
@Slf4j
public class ExerciseParametersService {
    private final ExerciseParametersRepository repository;

    public ExerciseParameters prepExerciseParameters(ExerciseParametersWrite parametersWrite) {
        var preparedParameters = NoDuplicationService.prepEntity(
            parametersWrite.toEntity(),
            repository,
            repository::save
        );
        if (preparedParameters == null) {
            log.info("Exercises must contain its parameters");
            throw new IllegalArgumentException("Exercises must contain its parameters");
        }
        return preparedParameters;
    }

    public boolean parametersAreOrphaned(ExerciseParametersRead parametersRead) {
        return !repository.referencedInExercise(parametersRead.getId()) &&
            !repository.referencedInTrainingExercise(parametersRead.getId());
    }

    public void deleteIfOrphaned(ExerciseParametersRead parametersRead) {
        if (parametersAreOrphaned(parametersRead))
            repository.deleteById(parametersRead.getId());
    }
}
