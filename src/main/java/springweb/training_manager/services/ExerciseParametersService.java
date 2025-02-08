package springweb.training_manager.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import springweb.training_manager.models.entities.ExerciseParameters;
import springweb.training_manager.models.view_models.exercise_parameters.ExerciseParametersRead;
import springweb.training_manager.models.view_models.exercise_parameters.ExerciseParametersWrite;
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

    public static int calcTotalBurnedKcal(
        int defaultBurnedKcal,
        ExerciseParametersRead parametersRead
    ) {
        return calcTotalBurnedKcalForRounds(
            parametersRead.getRounds(),
            defaultBurnedKcal,
            parametersRead
        );
    }

    public static int calcBurnedKcalPerRound(
        int defaultBurnedKcal,
        ExerciseParametersRead parametersRead
    ) {
        return calcTotalBurnedKcalForRounds(
            1,
            defaultBurnedKcal,
            parametersRead
        );
    }

    private static int calcTotalBurnedKcalForRounds(
        int rounds,
        int defaultBurnedKcal,
        ExerciseParametersRead parametersRead
    ) {
        float amount = parametersRead.getRepetition() != 0
            ? parametersRead.getRepetition()
            : (float) parametersRead.getTime()
            .toSecondOfDay() / 60;
        return (int) (
            Math.ceil(
                defaultBurnedKcal
                    * (amount * rounds)
                    * (1 + parametersRead.getWeights())
            )
        );
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
