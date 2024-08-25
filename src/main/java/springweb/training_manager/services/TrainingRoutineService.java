package springweb.training_manager.services;

import org.springframework.stereotype.Service;
import springweb.training_manager.models.entities.TrainingRoutine;
import springweb.training_manager.models.entities.User;
import springweb.training_manager.models.viewmodels.training_routine.TrainingRoutineRead;
import springweb.training_manager.repositories.for_controllers.TrainingRoutineRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrainingRoutineService {

    private final TrainingRoutineRepository repository;

    public TrainingRoutineService(
        final TrainingRoutineRepository repository
    ) {
        this.repository = repository;
    }

    public List<TrainingRoutineRead> getAll(){
        return repository.findAll()
        .stream().map(TrainingRoutineRead::new)
        .collect(Collectors.toList());
    }

    public TrainingRoutine getUserActiveRoutine(String userId){
        return repository.findByOwnerIdAndActiveTrue(userId)
        .orElseThrow(() -> new IllegalStateException("Użytkownik nie posiada aktywnej rutyny treningowej."));
    }

    public TrainingRoutine createNew(TrainingRoutine toSave){
        return repository.save(toSave);
    }
    public TrainingRoutine createNewByUser(User user){
        var routine = new TrainingRoutine(user);
        return createNew(routine);
    }

    public void delete(TrainingRoutine toDelete){
        repository.delete(toDelete);
    }
}
