package springweb.training_manager.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import springweb.training_manager.models.entities.DoneExercise;
import springweb.training_manager.repositories.for_controllers.DoneExerciseRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class DoneExerciseService {
    private final DoneExerciseRepository doneExerciseRepository;

    @Transactional
    public void createAll(List<DoneExercise> doneExercise) {
        doneExerciseRepository.saveAll(doneExercise);
    }
}
