package springweb.training_manager.services;

import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import springweb.training_manager.models.entities.Exercise;
import springweb.training_manager.models.entities.Training;
import springweb.training_manager.models.entities.User;
import springweb.training_manager.models.viewmodels.exercise.ExerciseTraining;
import springweb.training_manager.models.viewmodels.training.TrainingCreate;
import springweb.training_manager.models.viewmodels.training.TrainingRead;
import springweb.training_manager.models.viewmodels.training.TrainingWrite;
import springweb.training_manager.models.viewmodels.training.TrainingWriteAPI;
import springweb.training_manager.models.viewmodels.validation.ValidationErrors;
import springweb.training_manager.repositories.for_controllers.ExerciseRepository;
import springweb.training_manager.repositories.for_controllers.TrainingRepository;
import springweb.training_manager.repositories.for_controllers.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrainingService {
    private final ExerciseRepository exerciseRepository;
    private final TrainingRepository repository;
    private final UserRepository userRepository;

    /**
     * This method is used to find existing exercises in database or creating new one
     * corresponding to objects inside <code>exercises</code>. After that, they are
     * returned as a new list.
     *
     * @param exercises list of <code>ExerciseTraining</code> from <code>TrainingWrite</code> object.
     *                  Can be used e.g. in <code>create(TrainingWrite toSave)</code> method.
     *
     * @return prepared list with <code>ExerciseTraining</code> (founded in database or just created)
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

    public void setExercisesById(TrainingWrite toSave, String[] exercisesIds) {
        if (exercisesIds == null || exercisesIds.length == 0)
            return;
        List<ExerciseTraining> exercisesToSave = new ArrayList<>(exercisesIds.length);
        for (String exerciseID : exercisesIds) {
            if (exerciseID.isEmpty())
                continue;
            int id = Integer.parseInt(exerciseID);
            Exercise found = exerciseRepository.findById(id).get();
            ExerciseTraining viewModel = new ExerciseTraining(found);
            exercisesToSave.add(viewModel);
        }

        List<ExerciseTraining> currExercises = toSave.getExercises();
        currExercises.addAll(exercisesToSave);
        toSave.setExercises(currExercises);
    }

    public Map<String, List<String>> validateAndPrepareTraining(
        TrainingWriteAPI data,
        BindingResult result
    ) {
        var toSave = data.getToSave();

        if (result.hasErrors()) {
            var validation = ValidationErrors.createFrom(result, "toSave.");
            return validation.getErrors();
        }

        setExercisesById(toSave, data.getSelectedExercises());
        return null;
    }

    public Training create(TrainingWrite toSave, String userId) {
        List<Exercise> preparedExerciseList = prepExercises(toSave.getExercises());
        if (preparedExerciseList != null)
            toSave.setExercises(ExerciseTraining.toExerciseTrainingList(preparedExerciseList));

        var created = repository.save(toSave.toEntity());

        if (userId != null) {
            User loggedUser = userRepository.findById(userId)
                .orElseThrow(
                    () -> new IllegalArgumentException("Nie istnieje użytkownik o takim numerze ID.")
                );
            Set<Training> oldUsersTrainings = loggedUser.getTrainings();
            oldUsersTrainings.add(created);
            loggedUser.setTrainings(oldUsersTrainings);
            userRepository.save(loggedUser);
        }

        if (preparedExerciseList != null)
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
     * @param toEdit        list of <code>Exercise</code> objects which should be connected with <code>Training</code>
     * @param ifAdd         when true, it will add <code>toAddOrRemove</code> to <code>toEdit</code>, otherwise it will
     *                      remove <code>toAddOrRemove</code> from <code>toEdit</code>.
     */
    private void editTrainingInExercises(Training toAddOrRemove, List<Exercise> toEdit, boolean ifAdd) {
        if (toEdit == null)
            return;
        toEdit.forEach(
            exercise -> {
                if (ifAdd)
                    exercise.getTrainings().add(toAddOrRemove);
                else
                    exercise.getTrainings().remove(toAddOrRemove);
                exerciseRepository.save(exercise);
            }
        );
    }

    private void editTrainingInUsers(Training toAddOrRemove, Set<User> toEdit, boolean ifAdd) {
        if (toEdit == null)
            return;
        toEdit.forEach(
            user -> {
                if (ifAdd)
                    user.getTrainings().add(toAddOrRemove);
                else
                    user.getTrainings().remove(toAddOrRemove);
                userRepository.save(user);
            }
        );
    }

    public <TR> List<TR> getAll(Function<Training, TR> mapper) {
        return repository.findAll()
            .stream().map(mapper)
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
     * @see #getAll(Pageable page)
     */
    public Page<TrainingRead> getAllAlternative(Pageable page) {
        page = PageSortService.validateSort(
            Training.class,
            page,
            LoggerFactory.getLogger(TrainingService.class)
        );

        Page<Integer> allIds = repository.findAllIds(page);
        if (allIds.getContent().isEmpty())
            allIds = repository.findAllIds(
                PageRequest.of(
                    PageSortService.getPageNumber(allIds),
                    allIds.getSize(),
                    page.getSort()
                )
            );

        List<TrainingRead> toReturn = repository.findAllByIdIn(allIds.getContent())
            .stream().map(
                TrainingRead::new
            ).toList();

        return new PageImpl<>(toReturn, page, allIds.getTotalElements());
    }

    /**
     * Less effective than alternative approach of <code>getAllAlternative(Pageable page)</code> method.
     *
     * @param page request to get paged <code>TrainingRead</code>
     *
     * @return Paged list of <code>TrainingRead</code> objects.
     *
     * @see #getAllAlternative(Pageable page)
     */
    public Page<TrainingRead> getAll(Pageable page) {
        page = PageSortService.validateSort(
            Training.class,
            page,
            LoggerFactory.getLogger(TrainingService.class)
        );

        Page<TrainingRead> toReturn = repository.findAll(page).map(TrainingRead::new);
        if (toReturn.getContent().isEmpty())
            toReturn = repository.findAll(
                PageRequest.of(
                    PageSortService.getPageNumber(toReturn),
                    toReturn.getSize(),
                    page.getSort()
                )
            ).map(TrainingRead::new);

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
            found.getUsers().stream()
                .anyMatch(
                    user -> user.getId().equals(userId)
                )
        )
            return found;
        else
            throw new IllegalArgumentException("Nie masz dostępu do tego treningu.");
    }

    // TODO: Use it in controller when ROLE_USER registered, otherwise normal getAll
    public List<TrainingRead> getByUserId(String userId) {
        List<TrainingRead> usersTrainings = userRepository.findById(userId)
            .orElseThrow(
                () -> new IllegalArgumentException("Użytkownik o takim numerze id nie istnieje.")
            ).getTrainings().stream().map(
                training -> {
                    TrainingRead read = new TrainingRead(training);
                    return read;
                }
            ).collect(Collectors.toList());
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

    public void edit(TrainingWrite toEdit, int id, String userId) {
        List<Exercise> preparedExerciseList = prepExercises(toEdit.getExercises());
        toEdit.setExercises(ExerciseTraining.toExerciseTrainingList(preparedExerciseList));

        Training toSave = getById(id, userId);
        editTrainingInExercises(toSave, toSave.getExercises(), false);

        toSave.copy(toEdit.toEntity());
        var saved = repository.save(toSave);
        editTrainingInExercises(saved, saved.getExercises(), true);
    }

    public void delete(int id, String userId) {
        var toDelete = getById(id, userId);
        editTrainingInExercises(toDelete, toDelete.getExercises(), false);
        editTrainingInUsers(toDelete, toDelete.getUsers(), false);

        repository.deleteById(id);
    }

}
