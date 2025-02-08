package springweb.training_manager.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import springweb.training_manager.models.entities.DoneExercise;
import springweb.training_manager.models.entities.DoneTraining;
import springweb.training_manager.models.entities.TrainingExercise;
import springweb.training_manager.models.entities.User;
import springweb.training_manager.models.view_models.done_exercise.DoneExerciseWrite;
import springweb.training_manager.repositories.for_controllers.DoneExerciseRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
class DoneExerciseService {
    private final DoneExerciseRepository doneExerciseRepository;
    private final TrainingExerciseService trainingExerciseService;

    DoneExercise toEntity(
        DoneExerciseWrite doneExerciseWrite,
        DoneTraining doneTraining,
        User loggedUser
    ) {
        DoneExercise doneExercise = new DoneExercise();

        var trainingId = doneTraining.getTraining()
            .getId();
        var exerciseId = doneExerciseWrite.getExerciseId();
        TrainingExercise trainingExercise = trainingExerciseService.getByTrainingIdAndExerciseIdForUse(
            trainingId,
            exerciseId,
            loggedUser
        );
        doneExercise.setTrainingExercise(trainingExercise);

        doneExercise.setDoneTraining(doneTraining);
        doneExercise.setDoneSeries(doneExerciseWrite.getDoneSeries());

        return doneExercise;
    }

    List<DoneExercise> toEntities(
        List<DoneExerciseWrite> doneExercisesWrite,
        DoneTraining doneTraining,
        User loggedUser
    ) {
        return doneExercisesWrite.stream()
            .map(
                doneExerciseWrite -> toEntity(
                    doneExerciseWrite,
                    doneTraining,
                    loggedUser
                )
            )
            .toList();
    }

    @Transactional
    public void createAllForTrainingRegister(
        List<DoneExerciseWrite> doneExercisesWrite,
        DoneTraining doneTraining,
        User loggedUser
    ) {

        if (doneExercisesWrite == null || doneExercisesWrite.isEmpty())
            return;
        var mapped = toEntities(
            doneExercisesWrite,
            doneTraining,
            loggedUser
        );
        doneExerciseRepository.saveAll(mapped);
    }
}
