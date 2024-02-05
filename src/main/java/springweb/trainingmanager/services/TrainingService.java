package springweb.trainingmanager.services;

import org.springframework.stereotype.Service;
import springweb.trainingmanager.models.entities.Training;
import springweb.trainingmanager.repositories.forcontrollers.ExerciseRepository;
import springweb.trainingmanager.repositories.forcontrollers.TrainingRepository;

import java.util.List;
import java.util.Optional;

@Service
public class TrainingService {
    private final ExerciseService exerciseService;
    private final ExerciseRepository exerciseRepository;
    private final TrainingRepository repository;

    public TrainingService(
            final ExerciseService exerciseService,
            final ExerciseRepository exerciseRepository,
            final TrainingRepository repository
    ) {
        this.exerciseService = exerciseService;
        this.exerciseRepository = exerciseRepository;
        this.repository = repository;
    }

    public Training save(Training toSave){
        toSave
            .getExercises()
            .forEach(
                exercise -> exercise.setTraining(toSave)
            );
        return repository.save(toSave);
    }

    public List<Training> getAll(){
        return repository.findAll();
    }

    public Training getById(int id){
        return repository.findById(id)
            .orElseThrow(
                () -> new IllegalArgumentException("Nie znaleziono treningu o podanym numerze id")
            );
    }

    public void edit(Training toEdit, int id){
        Training toSave = getById(id);
        toSave.copy(toEdit);
        repository.save(toSave);
    }
}
