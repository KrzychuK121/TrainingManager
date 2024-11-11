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

    @Transactional
    public Training create(TrainingWrite toSave, String userId) {
        var preparedExerciseList = prepExercisesAndParametersMap(toSave.getExercises());

        var created = repository.save(toSave.toEntity());

        if (userId != null) {
            User loggedUser = userRepository.findById(userId)
                .orElseThrow(
                    () -> new NotOwnedByUserException("Nie istnieje użytkownik o takim numerze ID.")
                );
            Set<Training> oldUsersTrainings = loggedUser.getTrainings();
            oldUsersTrainings.add(created);
            loggedUser.setTrainings(oldUsersTrainings);
            userRepository.save(loggedUser);
        }

        if (preparedExerciseList != null)
            created.setTrainingExercises(
                trainingExerciseService.updateTrainingExerciseConnection(
                    created,
                    preparedExerciseList
                )
            );

        return created;
    }

    /**
     * This method <b>SHOULD</b> be used after creating/editing <code>Training</code>. It
     * is responsible for adding <code>toAddOrRemove</code> to every <code>toEdit</code>
     * element and then saving the changes in the database. This operation is required to
     * create proper many to many row between <code>Exercise</code> and
     * <code>Training</code>
     *
     * @param toAddOrRemove <code>Training</code> with id which should be connected with
     *                      <code>Exercise</code>
     * @param toEdit        list of <code>Exercise</code> objects which should be
     *                      connected with <code>Training</code>
     * @param ifAdd         when true, it will add <code>toAddOrRemove</code> to
     *                      <code>toEdit</code>, otherwise it will remove
     *                      <code>toAddOrRemove</code> from <code>toEdit</code>.
     */
    private void editTrainingInExercises(
        Training toAddOrRemove,
        List<Exercise> toEdit,
        boolean ifAdd
    ) {
        if (toEdit == null)
            return;
        toEdit.forEach(
            exercise -> {
                if (ifAdd)
                    exercise.getTrainings()
                        .add(toAddOrRemove);
                else
                    exercise.getTrainings()
                        .remove(toAddOrRemove);
                exerciseRepository.save(exercise);
            }
        );
    }

    private void editTrainingInUsers(
        Training toAddOrRemove,
        Set<User> toEdit,
        boolean ifAdd
    ) {
        if (toEdit == null)
            return;
        toEdit.forEach(
            user -> {
                if (ifAdd)
                    user.getTrainings()
                        .add(toAddOrRemove);
                else
                    user.getTrainings()
                        .remove(toAddOrRemove);
                userRepository.save(user);
            }
        );
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
            found.getUsers()
                .stream()
                .anyMatch(
                    user -> user.getId()
                        .equals(userId)
                )
        )
            return found;
        else
            throw new NotOwnedByUserException("Nie masz dostępu do tego treningu.");
    }

    // TODO: Use it in controller when ROLE_USER registered, otherwise normal getAll
    public List<TrainingRead> getByUserId(String userId) {
        List<TrainingRead> usersTrainings = userRepository.findById(userId)
            .orElseThrow(
                () -> new NotOwnedByUserException("Użytkownik o takim numerze id nie istnieje.")
            )
            .getTrainings()
            .stream()
            .map(TrainingRead::new)
            .collect(Collectors.toList());
        return usersTrainings;
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
    public void edit(TrainingWrite toEdit, int id, String userId) {
        Map<Exercise, ExerciseParameters> preparedExerciseList = prepExercisesAndParametersMap(
            toEdit.getExercises()
        );

        Training toSave = getById(id, userId);
        var oldParametersList = getOldParametersList(toSave);
        toSave.copy(toEdit.toEntity());

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
        editTrainingInUsers(toDelete, toDelete.getUsers(), false);

        trainingExerciseService.deleteByTrainingId(toDelete);
        repository.deleteById(id);
        parametersService.deleteIfOrphaned(oldParametersList);
    }

}
