package springweb.trainingmanager.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import springweb.trainingmanager.models.entities.Exercise;
import springweb.trainingmanager.models.entities.Training;
import springweb.trainingmanager.models.viewmodels.exercise.ExerciseTraining;
import springweb.trainingmanager.models.viewmodels.training.TrainingWrite;
import springweb.trainingmanager.repositories.forcontrollers.ExerciseRepository;
import springweb.trainingmanager.repositories.forcontrollers.TrainingRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class TrainingService {
    private final ExerciseRepository exerciseRepository;
    private final TrainingRepository repository;

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
    private List<Exercise> prepExercises(List<ExerciseTraining> exercises) {
        if(exercises == null || exercises.isEmpty())
            return null;

        List<Exercise> exerciseToSave = new ArrayList<>(exercises.size());
        exercises.forEach(
            exerciseTraining -> {
                Exercise found = exerciseRepository.findByExercise(exerciseTraining.toExercise())
                    .orElse(exerciseTraining.toExercise());

                Logger logger = LoggerFactory.getLogger(TrainingService.class);
                logger.info(
                    "id: " + + found.getId() + "\n" +
                    "name: " + found.getName() + "\n" +
                    "repetition: " + found.getRepetition() + "\n" +
                    "round: " + found.getRounds() + "\n"
                );

                if(found.getId() == 0){
                    var savedExercise = exerciseRepository.save(found);
                    exerciseToSave.add(savedExercise);
                }else
                    exerciseToSave.add(found);

            }
        );
        return exerciseToSave;
    }

    public Training create(TrainingWrite toSave){
        List<Exercise> preparedExerciseList = prepExercises(toSave.getExercises());
        if(preparedExerciseList != null)
            toSave.setExercises(ExerciseTraining.toExerciseTrainingList(preparedExerciseList));

        var created = repository.save(toSave.toTraining());
        if(preparedExerciseList != null)
            editTrainingInExercises(created, preparedExerciseList, true);

        return created;
    }

    /**
     * This method <b>SHOULD</b> be used after creating/editing <code>Training</code>.
     * It is responsible for adding <code>toAddOrRemove</code> to every <code>toEdit</code> element
     * and then saving the changes in the database. This operation is required to create proper
     * many to many row between <code>Exercise</code> and <code>Training</code>
     *
     * @param toAddOrRemove <code>Training</code> with id which should be connected with <code>Exercise</code>
     * @param toEdit list of <code>Exercise</code> objects which should be connected with <code>Training</code>
     * @param ifAdd when true, it will add <code>toAddOrRemove</code> to <code>toEdit</code>, otherwise it will
     *              remove <code>toAddOrRemove</code> from <code>toEdit</code>.
     */
    private void editTrainingInExercises(Training toAddOrRemove, List<Exercise> toEdit, boolean ifAdd){
        if(toEdit == null)
            return;
        toEdit.forEach(
            exercise -> {
                if(ifAdd)
                    exercise.getTrainings().add(toAddOrRemove);
                else
                    exercise.getTrainings().remove(toAddOrRemove);
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

    public void edit(TrainingWrite toEdit, int id){
        List<Exercise> preparedExerciseList = prepExercises(toEdit.getExercises());
        toEdit.setExercises(ExerciseTraining.toExerciseTrainingList(preparedExerciseList));

        Training toSave = getById(id);
        editTrainingInExercises(toSave, toSave.getExercises(), false);

        toSave.copy(toEdit.toTraining());
        var saved = repository.save(toSave);
        editTrainingInExercises(saved, saved.getExercises(), true);
    }

    public void delete(int id){
        var toDelete = getById(id);
        editTrainingInExercises(toDelete, toDelete.getExercises(), false);
        repository.deleteById(id);
    }

}
