package springweb.trainingmanager.services;

import org.springframework.stereotype.Service;
import springweb.trainingmanager.models.entities.Exercise;
import springweb.trainingmanager.repositories.forcontrollers.ExerciseRepository;

import java.util.List;

@Service
public class ExerciseService {
    private final ExerciseRepository repository;

    public ExerciseService(
        ExerciseRepository repository
    ) {
        this.repository = repository;
    }

    public Exercise create(Exercise toSave){
        return repository.save(toSave);
    }

    public List<Exercise> getAll(){
        return repository.findAll();
    }

    public Exercise getById(int id){
        return repository.findById(id)
            .orElseThrow(
                () -> new IllegalArgumentException("Nie znaleziono treningu o podanym numerze id")
            );
    }

    public void edit(Exercise toEdit, int id){
        Exercise toSave = getById(id);
        toSave.copy(toEdit);
        repository.save(toSave);
    }

}
