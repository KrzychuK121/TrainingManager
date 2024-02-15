package springweb.trainingmanager.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import springweb.trainingmanager.models.entities.Exercise;
import springweb.trainingmanager.models.entities.Training;
import springweb.trainingmanager.models.viewmodels.exercise.ExerciseTraining;
import springweb.trainingmanager.models.viewmodels.exercise.ExerciseWrite;
import springweb.trainingmanager.models.viewmodels.training.TrainingExercise;
import springweb.trainingmanager.models.viewmodels.training.TrainingWrite;
import springweb.trainingmanager.repositories.forcontrollers.ExerciseRepository;
import springweb.trainingmanager.repositories.forcontrollers.TrainingRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class TrainingService {
    private final ExerciseRepository exerciseRepository;
    private final TrainingRepository repository;
    private static final Logger logger = LoggerFactory.getLogger(TrainingService.class);

    public TrainingService(
            final ExerciseRepository exerciseRepository,
            final TrainingRepository repository
    ) {
        this.exerciseRepository = exerciseRepository;
        this.repository = repository;
    }

    /**
     * This method is used to find existing exercises in database or creating new one
     * corresponding to objects inside <code>exercises</code>. After that, they are
     * returned as a new list.
     *
     * @param exercises list of <code>ExerciseTraining</code> from <code>TrainingWrite</code> object.
     *                      Can be used e.g. in <code>create(TrainingWrite toSave)</code> method.
     *
     * @return prepared list with <code>ExerciseTraining</code> (founded in database or just created)
     */
    private List<ExerciseTraining> prepExercises(List<ExerciseTraining> exercises) {
        if(exercises == null || exercises.isEmpty())
            return null;

        List<ExerciseTraining> exerciseToSave = new ArrayList<>(exercises.size());
        exercises.forEach(
            exerciseTraining -> {
                Exercise found = exerciseRepository.findByExercise(exerciseTraining.toExercise())
                    .orElse(exerciseTraining.toExercise());

                if(found.getId() == 0){
                    logger.info("Nie znaleziono");
                    var savedExercise = exerciseRepository.save(found);
                    logger.info("Nowy id: " + savedExercise.getId());
                    exerciseToSave.add(new ExerciseTraining(savedExercise));
                }else{
                    logger.info("Znaleziono");
                    exerciseToSave.add(new ExerciseTraining(found));
                }

            }
        );
        return exerciseToSave;
    }

    public Training create(TrainingWrite toSave){
        List<ExerciseTraining> preparedExerciseList = prepExercises(toSave.getExercises());
        if(preparedExerciseList != null)
            toSave.setExercises(preparedExerciseList);

        var created = repository.save(toSave.toTraining());
        addTrainingToExercises(created, created.getExercises());

        return created;
    }

    /**
     * This method <b>SHOULD</b> be used after <code>create(TrainingWrite toSave)</code>.
     * It is responsible for adding <code>toAdd</code> to every <code>toEdit</code> element
     * and then saving the changes in the database. This operation is required to create proper
     * many to many row between <code>Exercise</code> and <code>Training</code>
     *
     * @param toAdd <code>Training</code> with id which should be connected with <code>Exercise</code>
     * @param toEdit list of <code>Exercise</code> objects which should be connected with <code>Training</code>
     */
    private void addTrainingToExercises(Training toAdd, List<Exercise> toEdit){
        toEdit.forEach(
            exercise -> {
                exercise.getTrainings().add(toAdd);
                exerciseRepository.save(exercise);
            }
        );
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
