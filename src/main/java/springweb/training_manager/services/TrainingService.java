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
import org.springframework.validation.FieldError;
import springweb.training_manager.exceptions.NotOwnedByUserException;
import springweb.training_manager.models.entities.Exercise;
import springweb.training_manager.models.entities.ExerciseParameters;
import springweb.training_manager.models.entities.Training;
import springweb.training_manager.models.entities.User;
import springweb.training_manager.models.schemas.RoleSchema;
import springweb.training_manager.models.viewmodels.exercise.ExerciseTraining;
import springweb.training_manager.models.viewmodels.exercise_parameters.ExerciseParametersRead;
import springweb.training_manager.models.viewmodels.exercise_parameters.ExerciseParametersWrite;
import springweb.training_manager.models.viewmodels.training.*;
import springweb.training_manager.models.viewmodels.training_exercise.CustomTrainingParametersWrite;
import springweb.training_manager.models.viewmodels.validation.ValidationErrors;
import springweb.training_manager.repositories.for_controllers.ExerciseRepository;
import springweb.training_manager.repositories.for_controllers.TrainingRepository;

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
    public List<Exercise> prepExercises(
        List<ExerciseTraining> exercises,
        User loggedUser
    ) {
        var prepared = NoDuplicationService.prepEntitiesWithWriteModel(
            exercises,
            exerciseRepository,
            exerciseRepository::save
        );

        var filtered = prepared.stream()
            .filter(
                exercise -> UserService.isPermittedToReadFor(
                    loggedUser,
                    exercise.getOwner()
                )
            )
            .toList();

        return filtered.isEmpty() ? null : filtered;
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
                        exerciseRepository,
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

    public void setExercisesById(
        TrainingWrite toSave,
        String[] exercisesIds,
        User loggedUser
    ) {
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
        setExercisesById(
            toSave,
            selectedExercises,
            loggedUser
        );
    }

    public void setExercisesById(
        TrainingWrite toSave,
        List<SelectedExerciseWrite> selectedExercises,
        User loggedUser
    ) {
        if (selectedExercises == null || selectedExercises.isEmpty())
            return;

        setExercisesBy(
            toSave,
            selectedExercises,
            loggedUser
        );
    }

    private void setExercisesBy(
        TrainingWrite toSave,
        List<SelectedExerciseWrite> selectedExercises,
        User loggedUser
    ) {
        List<CustomTrainingParametersWrite> currExercises = toSave.getExercises();
        List<CustomTrainingParametersWrite> toAdd = new ArrayList<>(selectedExercises.size());
        for (var selected : selectedExercises) {
            if (
                selected.getSelectedId()
                    .isEmpty()
            )
                continue;
            var exerciseID = selected.getSelectedId();
            int id = Integer.parseInt(exerciseID);

            Exercise found = exerciseRepository.findById(id)
                .orElse(null);

            if (
                found == null
                    || !UserService.isPermittedToReadFor(loggedUser, found.getOwner())
            )
                continue;
            toAdd.add(
                new CustomTrainingParametersWrite(
                    new ExerciseTraining(found),
                    selected.getParameters()
                )
            );
        }
        currExercises.addAll(toAdd);
        toSave.setExercises(currExercises);
    }

    public Map<String, List<String>> validateAndPrepareTraining(
        TrainingWriteAPI data,
        BindingResult result,
        User loggedUser
    ) {
        var toSave = data.getToSave();
        var selectedExercises = data.getSelectedExercises();

        if (
            toSave.getTitle()
                .contains("#")
        )
            result.addError(
                new FieldError(
                    "toSave",
                    "title",
                    "Tytuł nie może posiadać znaku specjalnego #"
                )
            );

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

        setExercisesById(
            toSave,
            data.getSelectedExercises(),
            loggedUser
        );

        return null;
    }

    @Transactional
    public Training create(TrainingWrite toSave, User loggedUser) {
        var preparedExerciseList = prepExercisesAndParametersMap(toSave.getExercises());

        var entityToSave = toSave.toEntity();
        if (
            toSave.isTrainingPrivate()
                && !UserService.userIsInRole(loggedUser, RoleSchema.ROLE_ADMIN)
        )
            entityToSave.setOwner(loggedUser);

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

    public static int getTotalBurnedKcal(TrainingRead training) {
        var sum = 0;
        if (training == null)
            return 0;
        var exercises = training.getExercises();
        if (
            exercises == null
                || exercises.isEmpty()
        )
            return 0;

        for (var exercise : exercises)
            sum += ExerciseService.getTotalBurnedKcal(exercise);
        return sum;
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

    public List<TrainingExerciseVM> getAllForUser(User user) {
        if (UserService.userIsInRole(user, RoleSchema.ROLE_ADMIN))
            return getAll(TrainingExerciseVM::new);
        return repository.findAllPublicOrOwnedBy(user.getId())
            .stream()
            .map(TrainingExerciseVM::new)
            .toList();
    }

    private Page<TrainingRead> getPageBy(
        Pageable page,
        Function<Pageable, Page<Training>> find
    ) {
        return PageSortService.getPageBy(
            Training.class,
            page,
            find,
            TrainingRead::new,
            log
        );
    }

    /**
     * More effective but less elegant than <code>getAll(Pageable page)</code> method.
     *
     * @param page request to get paged <code>TrainingRead</code>
     *
     * @return Paged list of <code>TrainingRead</code> objects.
     *
     * @see #getPagedAll(Pageable page)
     */
    public Page<TrainingRead> getPagedAllAlternative(Pageable page) {
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
     * @see #getPagedAllAlternative(Pageable page)
     */
    public Page<TrainingRead> getPagedAll(Pageable page) {
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

    public Page<TrainingRead> getPagedPublicOrOwnerBy(
        Pageable page,
        User user
    ) {
        return getPageBy(
            page,
            pageable -> repository.findPagedPublicOrOwnedBy(pageable, user.getId())
        );
    }

    public Page<TrainingRead> getPagedForUser(Pageable page, User user) {
        if (UserService.userIsInRole(user, RoleSchema.ROLE_ADMIN))
            return getPagedAllAlternative(page);
        return getPagedPublicOrOwnerBy(page, user);
    }

    public List<TrainingRead> getPublicOrOwnerBy(User user) {
        return repository.findAllPublicOrOwnedBy(user.getId())
            .stream()
            .map(TrainingRead::new)
            .toList();
    }

    public Training getById(int id) {
        return repository.findById(id)
            .orElseThrow(
                () -> new IllegalArgumentException("Nie znaleziono treningu o podanym numerze id.")
            );
    }

    public Training getByIdForUse(
        int id,
        User loggedUser
    ) {
        var found = getById(id);
        if (!UserService.isPermittedToReadFor(loggedUser, found.getOwner()))
            throw new NotOwnedByUserException(
                "This user(" + loggedUser.getUsername() + ") can't access " +
                    "training with id(" + id + ") which is private."
            );

        return found;
    }

    public Training getByIdForModify(
        int id,
        User loggedUser
    ) {
        var found = getById(id);
        if (!UserService.isPermittedToModifyFor(loggedUser, found.getOwner()))
            throw new NotOwnedByUserException(
                "This user(" + loggedUser.getUsername() + ") is not " +
                    "an owner of training with id(" + id + ")."
            );
        return found;
    }

    public List<TrainingRead> getByUserId(String userId) {
        return repository.findAllByOwnerId(userId)
            .orElseThrow(
                () -> new NotOwnedByUserException("User does not exist or owns no trainings.")
            )
            .stream()
            .map(TrainingRead::new)
            .collect(Collectors.toList());
    }

    public TrainingCreate getCreateModel(Integer id, User owner) {
        var allExerciseTrainings = UserService.userIsInRole(owner, RoleSchema.ROLE_ADMIN)
            ? exerciseRepository.findAll()
            .stream()
            .map(ExerciseTraining::new)
            .toList()
            : ExerciseTraining.toExerciseTrainingList(
            exerciseRepository.findPublicOrOwnedBy(
                owner.getId()
            )
        );
        if (id == null)
            return new TrainingCreate(allExerciseTrainings);
        try {
            return new TrainingCreate(
                new TrainingRead(
                    getByIdForModify(id, owner)
                ),
                allExerciseTrainings
            );
        } catch (IllegalArgumentException ex) {
            return new TrainingCreate(allExerciseTrainings);
        }
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
        User loggedUser
    ) {
        Map<Exercise, ExerciseParameters> preparedExerciseList = prepExercisesAndParametersMap(
            toEdit.getExercises()
        );

        Training toSave = getByIdForModify(id, loggedUser);
        var oldOwner = toSave.getOwner();
        var oldParametersList = getOldParametersList(toSave);

        if (toSave.getOwner() != null && !toEdit.isTrainingPrivate())
            preparedExerciseList.keySet()
                .forEach(
                    prepExercise -> {
                        prepExercise.setOwner(null);
                        exerciseRepository.save(prepExercise);
                    }
                );

        toSave.copy(toEdit.toEntity());
        toSave.setOwner(
            !toEdit.isTrainingPrivate()
                ? null
                : oldOwner
        );
        toSave.setTrainingExercises(null);

        var saved = repository.save(toSave);
        trainingExerciseService.updateTrainingExerciseConnection(
            saved,
            preparedExerciseList
        );
        parametersService.deleteIfOrphaned(oldParametersList);
    }

    @Transactional
    public void delete(int id, User loggedUser) {
        var toDelete = getByIdForModify(id, loggedUser);
        var oldParametersList = getOldParametersList(toDelete);

        trainingExerciseService.deleteByTrainingId(toDelete);
        repository.deleteById(id);
        parametersService.deleteIfOrphaned(oldParametersList);
    }

}
