package springweb.training_manager.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import springweb.training_manager.exceptions.NotOwnedByUserException;
import springweb.training_manager.models.entities.Exercise;
import springweb.training_manager.models.entities.ExerciseParameters;
import springweb.training_manager.models.entities.Training;
import springweb.training_manager.models.entities.User;
import springweb.training_manager.models.viewmodels.exercise.ExerciseTraining;
import springweb.training_manager.models.viewmodels.exercise_parameters.ExerciseParametersRead;
import springweb.training_manager.models.viewmodels.exercise_parameters.ExerciseParametersWrite;
import springweb.training_manager.models.viewmodels.training.*;
import springweb.training_manager.models.viewmodels.training_exercise.CustomTrainingParametersWrite;
import springweb.training_manager.models.viewmodels.validation.ValidationErrors;
import springweb.training_manager.repositories.for_controllers.ExerciseRepository;
import springweb.training_manager.repositories.for_controllers.TrainingRepository;
import springweb.training_manager.repositories.for_controllers.UserRepository;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainingService {
    private final ExerciseRepository exerciseRepository;
    private final ExerciseParametersService parametersService;
    private final TrainingRepository repository;
    private final UserRepository userRepository;
    private final TrainingExerciseService trainingExerciseService;

    /**
     * This method is used to find existing exercises in database or creating new one
     * corresponding to objects inside <code>exercises</code>. After that, they are
     * returned as a new list.
     *
     * @param exercises list of <code>ExerciseTraining</code> from
     *                  <code>TrainingWrite</code> object. Can be used e.g. in
     *                  <code>create(TrainingWrite toSave)</code> method.
     *
     * @return prepared list with <code>ExerciseTraining</code> (founded in database or
     * just created)
     */
    public List<Exercise> prepExercises(List<ExerciseTraining> exercises) {
        if (exercises == null || exercises.isEmpty())
            return null;

        List<Exercise> exerciseToSave = new ArrayList<>(exercises.size());
        exercises.forEach(
            exerciseTraining -> {
                Exercise found = exerciseRepository.findByExercise(exerciseTraining.toEntity())
                    .orElse(exerciseTraining.toEntity());

                if (found.getId() == 0) {
                    var savedExercise = exerciseRepository.save(found);
                    exerciseToSave.add(savedExercise);
                } else
                    exerciseToSave.add(found);

            }
        );
        return exerciseToSave;
    }

    public Map<Exercise, ExerciseParameters> prepExercisesAndParametersMap(
        List<CustomTrainingParametersWrite> customData
    ) {
        if (customData == null || customData.isEmpty())
            return null;

        return customData.stream()
            .collect(
                Collectors.toMap(
                    data -> NoDuplicationService.prepEntity(
                        data.getExerciseWrite()
                            .toEntity(),
                        exerciseRepository::findByExercise,
                        exerciseRepository::save
                    ),
                    data -> parametersService.prepExerciseParameters(
                        data.getParameters() != null
                            ? data.getParameters()
                            : data.getExerciseWrite()
                            .getExerciseParameters()
                    )
                )
            );
    }

    public void setExercisesById(TrainingWrite toSave, String[] exercisesIds) {
        if (exercisesIds == null || exercisesIds.length == 0)
            return;
        var selectedExercises = Arrays.stream(exercisesIds)
            .map(
                id -> new SelectedExerciseWrite(
                    id,
                    new ExerciseParametersWrite()
                )
            )
            .toList();
        setExercisesById(toSave, selectedExercises);
    }

    // TODO: Prepare exercise parameters and put them later into TrainingExercise
    public void setExercisesById(
        TrainingWrite toSave,
        List<SelectedExerciseWrite> selectedExercises
    ) {
        if (selectedExercises == null || selectedExercises.isEmpty())
            return;

        setExercisesBy(toSave, selectedExercises);
    }

    private void setExercisesBy(
        TrainingWrite toSave,
        List<SelectedExerciseWrite> selectedExercises
    ) {
        List<CustomTrainingParametersWrite> currExercises = toSave.getExercises();
        currExercises.addAll(
            selectedExercises.stream()
                .filter(
                    selected -> !selected.getSelectedId()
                        .isEmpty()
                )
                .map(
                    selected -> {
                        var exerciseID = selected.getSelectedId();
                        int id = Integer.parseInt(exerciseID);
                        Exercise found = exerciseRepository.findById(id)
                            .get();
                        return new CustomTrainingParametersWrite(
                            new ExerciseTraining(found),
                            selected.getParameters()
                        );
                    }
                )
                .toList()
        );
        toSave.setExercises(currExercises);
    }

    public Map<String, List<String>> validateAndPrepareTraining(
        TrainingWriteAPI data,
        BindingResult result
    ) {
        var toSave = data.getToSave();
        var selectedExercises = data.getSelectedExercises();

        if (result.hasErrors()) {
            var validation = ValidationErrors.createFrom(result, "toSave.");

            var newValidationErrors = new HashMap<String, List<String>>(validation.getErrors()
                .size());
            var prefix = "selectedExercises";
            for (int i = 0; i < selectedExercises.size(); i++) {
                var exerciseId = selectedExercises.get(i)
                    .getSelectedId();
                var oldPrefix = String.format("selectedExercises[%s].parameters.", i + "");
                var newPrefix = String.format("parameters.%s.", "" + exerciseId);
                validation.getErrors()
                    .forEach(
                        (field, errors) -> {
                            if (
                                field.startsWith(prefix)
                            )
                                newValidationErrors.put(
                                    field.replace(oldPrefix, newPrefix),
                                    errors
                                );
                            else
                                newValidationErrors.put(field, errors);
                        }
                    );

            }

            return new ValidationErrors(newValidationErrors).getErrors();
        }

        setExercisesById(toSave, data.getSelectedExercises());

        return null;
    }

    private User getUserById(String userId) {
        return userRepository.findById(userId)
            .orElseThrow(
                () -> new NotOwnedByUserException("Nie istnieje użytkownik o takim numerze ID.")
            );
    }

    @Transactional
    public Training create(TrainingWrite toSave, String userId) {
        var preparedExerciseList = prepExercisesAndParametersMap(toSave.getExercises());

        var entityToSave = toSave.toEntity();
        if (userId != null && toSave.isTrainingPrivate()) {
            var loggedUser = getUserById(userId);
            entityToSave.setOwner(loggedUser);
        }

        var created = repository.save(entityToSave);

        if (preparedExerciseList != null)
            created.setTrainingExercises(
                trainingExerciseService.updateTrainingExerciseConnection(
                    created,
                    preparedExerciseList
                )
            );

        return created;
    }

    public <TR> List<TR> getAll(Function<Training, TR> mapper) {
        return repository.findAll()
            .stream()
            .map(mapper)
            .collect(Collectors.toList());
    }

    public List<TrainingRead> getAll() {
        return getAll(TrainingRead::new);
    }

    /**
     * More effective but less elegant than <code>getAll(Pageable page)</code> method.
     *
     * @param page request to get paged <code>TrainingRead</code>
     *
     * @return Paged list of <code>TrainingRead</code> objects.
     *
     * @see #getAllPaged(Pageable page)
     */
    public Page<TrainingRead> getAllPagedAlternative(Pageable page) {
        page = PageSortService.validateSort(
            Training.class,
            page,
            LoggerFactory.getLogger(TrainingService.class)
        );

        Page<Integer> allIds = repository.findAllIds(page);
        if (allIds.getContent()
            .isEmpty())
            allIds = repository.findAllIds(
                PageRequest.of(
                    PageSortService.getPageNumber(allIds),
                    allIds.getSize(),
                    page.getSort()
                )
            );

        List<TrainingRead> toReturn = repository.findAllByIdIn(allIds.getContent())
            .stream()
            .map(
                TrainingRead::new
            )
            .toList();

        return new PageImpl<>(toReturn, page, allIds.getTotalElements());
    }

    /**
     * Less effective than alternative approach of <code>getAllAlternative(Pageable
     * page)</code> method.
     *
     * @param page request to get paged <code>TrainingRead</code>
     *
     * @return Paged list of <code>TrainingRead</code> objects.
     *
     * @see #getAllPagedAlternative(Pageable page)
     */
    public Page<TrainingRead> getAllPaged(Pageable page) {
        page = PageSortService.validateSort(
            Training.class,
            page,
            LoggerFactory.getLogger(TrainingService.class)
        );

        Page<TrainingRead> toReturn = repository.findAll(page)
            .map(TrainingRead::new);
        if (toReturn.getContent()
            .isEmpty())
            toReturn = repository.findAll(
                    PageRequest.of(
                        PageSortService.getPageNumber(toReturn),
                        toReturn.getSize(),
                        page.getSort()
                    )
                )
                .map(TrainingRead::new);

        return toReturn;
    }

    public Training getById(int id, String userId) {
        Training found = repository.findById(id)
            .orElseThrow(
                () -> new IllegalArgumentException("Nie znaleziono treningu o podanym numerze id.")
            );

        if (userId == null)
            return found;

        if (
            found.getOwner()
                .getId()
                .equals(userId)
        )
            return found;
        else
            throw new NotOwnedByUserException("Nie masz dostępu do tego treningu.");
    }

    public List<TrainingRead> getPublicOrOwnerBy(String userId) {
        return repository.findAllPublicOrOwnedBy(userId)
            .orElse(new ArrayList<>())
            .stream()
            .map(TrainingRead::new)
            .toList();
    }

    // TODO: Use it in controller when ROLE_USER registered, otherwise normal getAll
    public List<TrainingRead> getByUserId(String userId) {
        return repository.findAllByOwnerId(userId)
            .orElseThrow(
                () -> new NotOwnedByUserException("User does not exist or owns no trainings.")
            )
            .stream()
            .map(TrainingRead::new)
            .collect(Collectors.toList());
    }

    public TrainingCreate getCreateModel(Integer id, String userId) {
        var allExerciseTrainings = ExerciseTraining.toExerciseTrainingList(exerciseRepository.findAll());
        if (id == null)
            return new TrainingCreate(allExerciseTrainings);
        try {
            return new TrainingCreate(new TrainingRead(getById(id, userId)), allExerciseTrainings);
        } catch (IllegalArgumentException ex) {
            return new TrainingCreate(allExerciseTrainings);
        }
    }

    public boolean existsById(int trainingId) {
        return repository.existsById(trainingId);
    }

    private static List<ExerciseParametersRead> getOldParametersList(Training toDelete) {
        return toDelete.getTrainingExercises()
            .stream()
            .map(
                trainingExercise -> new ExerciseParametersRead(
                    trainingExercise.getParameters()
                )
            )
            .toList();
    }

    @Transactional
    public void edit(
        TrainingWrite toEdit,
        int id,
        String ownerId
    ) {
        Map<Exercise, ExerciseParameters> preparedExerciseList = prepExercisesAndParametersMap(
            toEdit.getExercises()
        );

        Training toSave = getById(id, ownerId);
        var owner = toSave.getOwner();
        var oldParametersList = getOldParametersList(toSave);

        toSave.copy(toEdit.toEntity());
        toSave.setOwner(
            !toEdit.isTrainingPrivate()
                ? null
                : owner
        );
        toSave.setOwner(null);
        toSave.setTrainingExercises(null);

        var saved = repository.save(toSave);
        trainingExerciseService.updateTrainingExerciseConnection(
            saved,
            preparedExerciseList
        );
        parametersService.deleteIfOrphaned(oldParametersList);
    }

    @Transactional
    public void delete(int id, String userId) {
        var toDelete = getById(id, userId);
        var oldParametersList = getOldParametersList(toDelete);

        trainingExerciseService.deleteByTrainingId(toDelete);
        repository.deleteById(id);
        parametersService.deleteIfOrphaned(oldParametersList);
    }

}
