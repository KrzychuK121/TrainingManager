package springweb.training_manager.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import springweb.training_manager.models.entities.Exercise;
import springweb.training_manager.models.entities.Training;
import springweb.training_manager.models.viewmodels.exercise.ExerciseCreate;
import springweb.training_manager.models.viewmodels.exercise.ExerciseRead;
import springweb.training_manager.models.viewmodels.exercise.ExerciseWrite;
import springweb.training_manager.models.viewmodels.exercise.ExerciseWriteAPI;
import springweb.training_manager.models.viewmodels.exercise_parameters.ExerciseParametersRead;
import springweb.training_manager.models.viewmodels.training.TrainingExerciseVM;
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
    private final ExerciseParametersService parametersService;
    private final TrainingExerciseService trainingExerciseService;
    private final TrainingRepository trainingRepository;
    private final TrainingService trainingService;
    private static final Logger logger = LoggerFactory.getLogger(ExerciseService.class);

    /**
     * This method is used to find existing trainings in database or creating new one
     * corresponding to objects inside <code>trainings</code>. After that, they are
     * returned as a new list.
     *
     * @param trainings list of <code>TrainingExerciseVM</code> from
     *                  <code>ExerciseWrite</code> object. Can be used e.g. in
     *                  <code>create(ExerciseWrite toSave)</code> method.
     *
     * @return prepared list with
     * <code>TrainingExerciseVM</code> (founded in database
     * or just created)
     */
    private List<Training> prepTrainings(List<TrainingExerciseVM> trainings) {
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
            List<TrainingExerciseVM> trainingsToSave = new ArrayList<>(trainingIds.length);
            for (String trainingID : trainingIds) {
                if (trainingID.isEmpty())
                    continue;
                int id = Integer.parseInt(trainingID);
                Training found = trainingRepository.findById(id)
                    .get();
                TrainingExerciseVM viewModel = new TrainingExerciseVM(found);
                trainingsToSave.add(viewModel);
            }
            toSave.setTrainings(trainingsToSave);
        }
    }

    public static void setTime(ExerciseWrite toSave) {
        var parameters = toSave.getParameters();
        var validTime = parameters.getTime() != null
            ? parameters.getTime()
            .toString()
            : null;
        setTime(toSave, validTime);
    }

    public static void setTime(ExerciseWrite toSave, String time) {
        var formattedTime = TimeService.parseTime(time);
        if (formattedTime != null)
            toSave.setTime(formattedTime);
    }

    public static String[] getToEditTrainingIds(ExerciseRead toEdit) {
        List<TrainingExerciseVM> toEditList = toEdit.getTrainings();
        String[] selected = new String[toEditList.size()];
        for (int i = 0; i < toEditList.size(); i++) {
            selected[i] = toEditList.get(i)
                .getId() + "";
        }
        return selected;
    }

    public Map<String, List<String>> validateAndPrepareExercise(
        ExerciseWriteAPI data,
        BindingResult result
    ) {
        final var ENTITY_PREFIX = "toSave.";
        var toSave = data.getToSave();

        if (result.hasErrors()) {
            var validation = ValidationErrors.createFrom(result, ENTITY_PREFIX);
            return validation.getErrors();
        }

        setTrainingsById(toSave, data.getSelectedTrainings());
        setTime(toSave);
        return null;
    }

    @Transactional
    public Exercise create(ExerciseWrite toSave) {
        var preparedParameters = parametersService.prepExerciseParameters(
            toSave.getParameters()
        );
        List<Training> preparedTrainingList = prepTrainings(toSave.getTrainings());

        var entityToSave = toSave.toEntity();
        entityToSave.setParameters(preparedParameters);

        var created = repository.save(entityToSave);
        if (preparedTrainingList != null)
            created.setTrainingExercises(
                trainingExerciseService.updateTrainingExerciseConnection(
                    created,
                    preparedTrainingList
                )
            );

        return created;
    }

    /**
     * This method <b>SHOULD</b> be used after creating/editing <code>Exercise</code>. It
     * is responsible for adding <code>toAddOrRemove</code> to every <code>toEdit</code>
     * element and then saving the changes in the database. This operation is required to
     * create proper many to many row between
     * <code>Exercise</code> and <code>Training</code>
     *
     * @param toAddOrRemove <code>Exercise</code> with id which should be connected with
     *                      <code>Training</code>
     * @param toEdit        list of <code>Training</code> objects which should be
     *                      connected with <code>Exercise</code>
     * @param ifAdd         when true, it will add <code>toAddOrRemove</code> to
     *                      <code>toEdit</code>, otherwise it will remove
     *                      <code>toAddOrRemove</code> from <code>toEdit</code>.
     */
    private void editExerciseInTrainings(
        Exercise toAddOrRemove,
        List<Training> toEdit,
        boolean ifAdd
    ) {
        if (toEdit == null)
            return;
        toEdit.forEach(
            training -> {
                if (ifAdd)
                    training.getExercises()
                        .add(toAddOrRemove);
                else
                    training.getExercises()
                        .remove(toAddOrRemove);
            }
        );
    }

    public Page<ExerciseRead> getAll(Pageable page) {
        page = PageSortService.validateSort(Exercise.class, page, logger);

        Page<ExerciseRead> toReturn = repository.findAll(page)
            .map(ExerciseRead::new);
        if (toReturn.getContent()
            .isEmpty())
            toReturn = repository.findAll(
                    PageRequest.of(
                        PageSortService.getPageNumber(toReturn),
                        toReturn.getSize(),
                        page.getSort()
                    )
                )
                .map(ExerciseRead::new);
        return toReturn;
    }

    public Exercise getById(int id) {
        return repository.findById(id)
            .orElseThrow(
                () -> new IllegalArgumentException("Nie znaleziono ćwiczenia o podanym numerze id")
            );
    }

    public ExerciseCreate getCreateModel(Integer id) {
        var allTrainings = trainingService.getAll(TrainingExerciseVM::new);
        if (id == null)
            return new ExerciseCreate(allTrainings);
        try {
            return new ExerciseCreate(new ExerciseRead(getById(id)), allTrainings);
        } catch (IllegalArgumentException ex) {
            return new ExerciseCreate(allTrainings);
        }
    }

    @Transactional
    public void edit(ExerciseWrite toEdit, int id) {
        List<Training> preparedTrainingList = prepTrainings(toEdit.getTrainings());
        var preparedParameters = parametersService.prepExerciseParameters(
            toEdit.getParameters()
        );

        Exercise toSave = getById(id);
        var oldParametersRead = new ExerciseParametersRead(
            toSave.getParameters()
        );
        toSave.copy(toEdit.toEntity());
        toSave.setParameters(preparedParameters);

        var saved = repository.save(toSave);
        trainingExerciseService.updateTrainingExerciseConnection(
            saved,
            preparedTrainingList
        );
        parametersService.deleteIfOrphaned(oldParametersRead);
    }

    @Transactional
    public void delete(int id) {
        var toDelete = getById(id);
        var oldParametersRead = new ExerciseParametersRead(
            toDelete.getParameters()
        );
        trainingExerciseService.deleteByExerciseId(toDelete);
        repository.deleteById(id);
        parametersService.deleteIfOrphaned(oldParametersRead);
    }
}
