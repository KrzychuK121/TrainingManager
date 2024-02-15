package springweb.trainingmanager.services;

import org.springframework.stereotype.Service;
import springweb.trainingmanager.models.entities.Exercise;
import springweb.trainingmanager.models.entities.Training;
import springweb.trainingmanager.models.viewmodels.exercise.ExerciseWrite;
import springweb.trainingmanager.models.viewmodels.training.TrainingExercise;
import springweb.trainingmanager.repositories.forcontrollers.ExerciseRepository;
import springweb.trainingmanager.repositories.forcontrollers.TrainingRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class ExerciseService {
    private final ExerciseRepository repository;
    private final TrainingRepository trainingRepository;

    public ExerciseService(
        final ExerciseRepository repository,
        final TrainingRepository trainingRepository
    ) {
        this.repository = repository;
        this.trainingRepository = trainingRepository;
    }

    /**
     * This method is used to find existing trainings in database or creating new one
     * corresponding to objects inside <code>trainings</code>. After that, they are
     * returned as a new list.
     *
     * @param trainings list of <code>TrainingExercise</code> from <code>ExerciseWrite</code> object.
 *                      Can be used e.g. in <code>create(ExerciseWrite toSave)</code> method.
     *
     * @return prepared list with <code>TrainingExercise</code> (founded in database or just created)
     */
    private List<TrainingExercise> prepTrainings(List<TrainingExercise> trainings){
        if(trainings == null || trainings.isEmpty())
            return null;

        List<TrainingExercise> trainingToSave = new ArrayList<>(trainings.size());
        trainings.forEach(
            trainingExercise -> {
                Training found = trainingRepository.findByTraining(trainingExercise.toTraining())
                .orElse(trainingExercise.toTraining());

                if(found.getId() == 0){
                    var savedTraining = trainingRepository.save(found);
                    trainingToSave.add(new TrainingExercise(savedTraining, savedTraining.getId()));
                }else
                    trainingToSave.add(new TrainingExercise(found, found.getId()));

            }
        );
        return trainingToSave;
    }

    public Exercise create(ExerciseWrite toSave){
        List<TrainingExercise> preparedTrainingList = prepTrainings(toSave.getTrainings());
        if(preparedTrainingList != null)
            toSave.setTrainings(preparedTrainingList);

        var created = repository.save(toSave.toExercise());
        addExerciseToTrainings(created, created.getTrainings());

        return created;
    }

    /**
     * This method <b>SHOULD</b> be used after <code>create(ExerciseWrite toSave)</code>.
     * It is responsible for adding <code>toAdd</code> to every <code>toEdit</code> element
     * and then saving the changes in the database. This operation is required to create proper
     * many to many row between <code>Exercise</code> and <code>Training</code>
     *
     * @param toAdd <code>Exercise</code> with id which should be connected with <code>Training</code>
     * @param toEdit list of <code>Training</code> objects which should be connected with <code>Exercise</code>
     */
    private void addExerciseToTrainings(Exercise toAdd, List<Training> toEdit){
        toEdit.forEach(
            training -> {
                training.getExercises().add(toAdd);
                trainingRepository.save(training);
            }
        );
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
