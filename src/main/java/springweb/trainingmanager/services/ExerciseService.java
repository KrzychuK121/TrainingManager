package springweb.trainingmanager.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import springweb.trainingmanager.models.entities.Exercise;
import springweb.trainingmanager.models.entities.Training;
import springweb.trainingmanager.models.viewmodels.exercise.ExerciseRead;
import springweb.trainingmanager.models.viewmodels.exercise.ExerciseWrite;
import springweb.trainingmanager.models.viewmodels.training.TrainingExercise;
import springweb.trainingmanager.repositories.forcontrollers.ExerciseRepository;
import springweb.trainingmanager.repositories.forcontrollers.TrainingRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExerciseService {
    private final ExerciseRepository repository;
    private final TrainingRepository trainingRepository;
    private static final Logger logger = LoggerFactory.getLogger(ExerciseService.class);

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
    private List<Training> prepTrainings(List<TrainingExercise> trainings){
        if(trainings == null || trainings.isEmpty())
            return null;

        List<Training> trainingToSave = new ArrayList<>(trainings.size());
        trainings.forEach(
            trainingExercise -> {
                Training found = trainingRepository.findByTraining(trainingExercise.toTraining())
                    .orElse(trainingExercise.toTraining());

                if(found.getId() == 0){
                    var savedTraining = trainingRepository.save(found);
                    trainingToSave.add(savedTraining);
                }else
                    trainingToSave.add(found);

            }
        );
        return trainingToSave;
    }

    public Exercise create(ExerciseWrite toSave){
        List<Training> preparedTrainingList = prepTrainings(toSave.getTrainings());
        if(preparedTrainingList != null)
            toSave.setTrainings(TrainingExercise.toTrainingExerciseList(preparedTrainingList));

        var created = repository.save(toSave.toExercise());
        if(preparedTrainingList != null)
            editExerciseInTrainings(created, preparedTrainingList, true);

        return created;
    }

    /**
     * This method <b>SHOULD</b> be used after creating/editing <code>Exercise</code>.
     * It is responsible for adding <code>toAddOrRemove</code> to every <code>toEdit</code> element
     * and then saving the changes in the database. This operation is required to create proper
     * many to many row between <code>Exercise</code> and <code>Training</code>
     *
     * @param toAddOrRemove <code>Exercise</code> with id which should be connected with <code>Training</code>
     * @param toEdit list of <code>Training</code> objects which should be connected with <code>Exercise</code>
     * @param ifAdd when true, it will add <code>toAddOrRemove</code> to <code>toEdit</code>, otherwise it will
     *              remove <code>toAddOrRemove</code> from <code>toEdit</code>.
     */
    private void editExerciseInTrainings(Exercise toAddOrRemove, List<Training> toEdit, boolean ifAdd){
        if(toEdit == null)
            return;
        toEdit.forEach(
            training -> {
                if(ifAdd)
                    training.getExercises().add(toAddOrRemove);
                else
                    training.getExercises().remove(toAddOrRemove);
                trainingRepository.save(training);
            }
        );
    }

    public Page<ExerciseRead> getAll(Pageable page){
        page = PageSortService.validateSort(Exercise.class, page, logger);

        Page<ExerciseRead> toReturn = repository.findAll(page).map(ExerciseRead::new);
        if(toReturn.getContent().isEmpty())
            toReturn = repository.findAll(
                PageRequest.of(
                    PageSortService.getPageNumber(toReturn),
                    toReturn.getSize(),
                    page.getSort()
                )
            ).map(ExerciseRead::new);
        return toReturn;
    }

    public Exercise getById(int id){
        return repository.findById(id)
            .orElseThrow(
                () -> new IllegalArgumentException("Nie znaleziono ćwiczenia o podanym numerze id")
            );
    }

    public void edit(ExerciseWrite toEdit, int id){
        List<Training> preparedTrainingList = prepTrainings(toEdit.getTrainings());
        toEdit.setTrainings(TrainingExercise.toTrainingExerciseList(preparedTrainingList));

        Exercise toSave = getById(id);
        editExerciseInTrainings(toSave, toSave.getTrainings(), false);

        toSave.copy(toEdit.toExercise());
        var saved = repository.save(toSave);
        editExerciseInTrainings(saved, saved.getTrainings(), true);
    }

    public void delete(int id){
        var toDelete = getById(id);
        editExerciseInTrainings(toDelete, toDelete.getTrainings(), false);
        repository.deleteById(id);
    }
}
