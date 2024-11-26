package springweb.training_manager.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import springweb.training_manager.models.entities.DoneExercise;
import springweb.training_manager.models.entities.DoneTraining;
import springweb.training_manager.models.entities.TrainingExercise;
import springweb.training_manager.models.viewmodels.done_exercise.DoneExerciseWrite;
import springweb.training_manager.repositories.for_controllers.DoneExerciseRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
class DoneExerciseService {
    private final DoneExerciseRepository doneExerciseRepository;
    private final TrainingExerciseService trainingExerciseService;

    public DoneExercise toEntity(
        DoneExerciseWrite doneExerciseWrite,
        DoneTraining doneTraining
    ) {
        DoneExercise doneExercise = new DoneExercise();

        var trainingExerciseId = doneExerciseWrite.getTrainingExerciseId();
        TrainingExercise trainingExercise = trainingExerciseService.getByIdForUse(trainingExerciseId);
        doneExercise.setTrainingExercise(trainingExercise);

        doneExercise.setDoneTraining(doneTraining);
        doneExercise.setDoneSeries(doneExerciseWrite.getDoneSeries());

        return doneExercise;
    }

    @Transactional
    public void createAllForTrainingRegister(
        List<DoneExerciseWrite> doneExercise,
        DoneTraining doneTraining
    ) {
        var mapped = doneExercise.stream()
            .map(doneExerciseWrite -> toEntity(doneExerciseWrite, doneTraining))
            .toList();
        doneExerciseRepository.saveAll(mapped);
    }
}
