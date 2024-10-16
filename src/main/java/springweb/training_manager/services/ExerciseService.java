package springweb.training_manager.services;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import springweb.training_manager.models.entities.Exercise;
import springweb.training_manager.models.entities.Training;
import springweb.training_manager.models.viewmodels.exercise.ExerciseCreate;
import springweb.training_manager.models.viewmodels.exercise.ExerciseRead;
import springweb.training_manager.models.viewmodels.exercise.ExerciseWrite;
import springweb.training_manager.models.viewmodels.exercise.ExerciseWriteAPI;
import springweb.training_manager.models.viewmodels.training.TrainingExercise;
import springweb.training_manager.models.viewmodels.validation.ValidationErrors;
import springweb.training_manager.repositories.for_controllers.ExerciseRepository;
import springweb.training_manager.repositories.for_controllers.TrainingRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExerciseService {
    private final ExerciseRepository repository;
    private final TrainingRepository trainingRepository;
    private final TrainingService trainingService;
    private static final Logger logger = LoggerFactory.getLogger(ExerciseService.class);

    /**
     * This method is used to find existing trainings in database or creating new one
     * corresponding to objects inside <code>trainings</code>. After that, they are
     * returned as a new list.
     *
     * @param trainings list of <code>TrainingExercise</code> from <code>ExerciseWrite</code> object.
     *                  Can be used e.g. in <code>create(ExerciseWrite toSave)</code> method.
     *
     * @return prepared list with <code>TrainingExercise</code> (founded in database or just created)
     */
    private List<Training> prepTrainings(List<TrainingExercise> trainings) {
        if (trainings == null || trainings.isEmpty())
            return null;

        List<Training> trainingToSave = new ArrayList<>(trainings.size());
        trainings.forEach(
            trainingExercise -> {
                Training found = trainingRepository.findByTraining(trainingExercise.toEntity())
                    .orElse(trainingExercise.toEntity());

                if (found.getId() == 0) {
                    var savedTraining = trainingRepository.save(found);
                    trainingToSave.add(savedTraining);
                } else
                    trainingToSave.add(found);

            }
        );
        return trainingToSave;
    }

    public void setTrainingsById(ExerciseWrite toSave, String[] trainingIds) {
        if (trainingIds != null && trainingIds.length != 0) {
            List<TrainingExercise> trainingsToSave = new ArrayList<>(trainingIds.length);
            for (String trainingID : trainingIds) {
                if (trainingID.isEmpty())
                    continue;
                int id = Integer.parseInt(trainingID);
                Training found = trainingRepository.findById(id).get();
                TrainingExercise viewModel = new TrainingExercise(found);
                trainingsToSave.add(viewModel);
            }
            toSave.setTrainings(trainingsToSave);
        }
    }

    public static void setTime(ExerciseWrite toSave) {
        var validTime = toSave.getTime() != null
            ? toSave.getTime().toString()
            : null;
        setTime(toSave, validTime);
    }

    public static void setTime(ExerciseWrite toSave, String time) {
        var formattedTime = TimeService.parseTime(time);
        if (formattedTime != null)
            toSave.setTime(formattedTime);
    }

    public static String[] getToEditTrainingIds(ExerciseRead toEdit) {
        List<TrainingExercise> toEditList = toEdit.getTrainings();
        String[] selected = new String[toEditList.size()];
        for (int i = 0; i < toEditList.size(); i++) {
            selected[i] = toEditList.get(i).getId() + "";
        }
        return selected;
    }

    public Map<String, List<String>> validateAndPrepareExercise(
        ExerciseWriteAPI data,
        BindingResult result
    ) {
        var toSave = data.getToSave();

        if (toSave.getRepetition() == 0 && toSave.getTime() == null) {
            var message = "Wpisz wartość w polu 'Powtórzenia' lub 'Czas wykonania'";
            result.addError(new FieldError("data", "toSave.repetition", message));
            result.addError(new FieldError("data", "toSave.time", message));
        }

        if (result.hasErrors()) {
            var validation = ValidationErrors.createFrom(result, "toSave.");
            return validation.getErrors();
        }

        setTrainingsById(toSave, data.getSelectedTrainings());
        setTime(toSave);
        return null;
    }

    public Exercise create(ExerciseWrite toSave) {
        List<Training> preparedTrainingList = prepTrainings(toSave.getTrainings());
        if (preparedTrainingList != null)
            toSave.setTrainings(TrainingExercise.toTrainingExerciseList(preparedTrainingList));

        var created = repository.save(toSave.toEntity());
        if (preparedTrainingList != null)
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
     * @param toEdit        list of <code>Training</code> objects which should be connected with <code>Exercise</code>
     * @param ifAdd         when true, it will add <code>toAddOrRemove</code> to <code>toEdit</code>, otherwise it will
     *                      remove <code>toAddOrRemove</code> from <code>toEdit</code>.
     */
    private void editExerciseInTrainings(Exercise toAddOrRemove, List<Training> toEdit, boolean ifAdd) {
        if (toEdit == null)
            return;
        toEdit.forEach(
            training -> {
                if (ifAdd)
                    training.getExercises().add(toAddOrRemove);
                else
                    training.getExercises().remove(toAddOrRemove);
                trainingRepository.save(training);
            }
        );
    }

    public Page<ExerciseRead> getAll(Pageable page) {
        page = PageSortService.validateSort(Exercise.class, page, logger);

        Page<ExerciseRead> toReturn = repository.findAll(page).map(ExerciseRead::new);
        if (toReturn.getContent().isEmpty())
            toReturn = repository.findAll(
                PageRequest.of(
                    PageSortService.getPageNumber(toReturn),
                    toReturn.getSize(),
                    page.getSort()
                )
            ).map(ExerciseRead::new);
        return toReturn;
    }

    public Exercise getById(int id) {
        return repository.findById(id)
            .orElseThrow(
                () -> new IllegalArgumentException("Nie znaleziono ćwiczenia o podanym numerze id")
            );
    }

    public ExerciseCreate getCreateModel(Integer id) {
        var allTrainings = trainingService.getAll(TrainingExercise::new);
        if (id == null)
            return new ExerciseCreate(allTrainings);
        try {
            return new ExerciseCreate(new ExerciseRead(getById(id)), allTrainings);
        } catch (IllegalArgumentException ex) {
            return new ExerciseCreate(allTrainings);
        }
    }

    public void edit(ExerciseWrite toEdit, int id) {
        List<Training> preparedTrainingList = prepTrainings(toEdit.getTrainings());
        toEdit.setTrainings(TrainingExercise.toTrainingExerciseList(preparedTrainingList));

        Exercise toSave = getById(id);
        editExerciseInTrainings(toSave, toSave.getTrainings(), false);

        toSave.copy(toEdit.toEntity());
        var saved = repository.save(toSave);
        editExerciseInTrainings(saved, saved.getTrainings(), true);
    }

    public void delete(int id) {
        var toDelete = getById(id);
        editExerciseInTrainings(toDelete, toDelete.getTrainings(), false);
        repository.deleteById(id);
    }
}
