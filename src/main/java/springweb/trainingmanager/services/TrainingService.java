package springweb.trainingmanager.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import springweb.trainingmanager.models.entities.Exercise;
import springweb.trainingmanager.models.entities.Training;
import springweb.trainingmanager.models.entities.User;
import springweb.trainingmanager.models.viewmodels.exercise.ExerciseTraining;
import springweb.trainingmanager.models.viewmodels.training.TrainingRead;
import springweb.trainingmanager.models.viewmodels.training.TrainingWrite;
import springweb.trainingmanager.repositories.forcontrollers.ExerciseRepository;
import springweb.trainingmanager.repositories.forcontrollers.TrainingRepository;
import springweb.trainingmanager.repositories.forcontrollers.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TrainingService {
    private final ExerciseRepository exerciseRepository;
    private final TrainingRepository repository;
    private final UserRepository userRepository;

    public TrainingService(
        final ExerciseRepository exerciseRepository,
        final TrainingRepository repository,
        final UserRepository userRepository
    ) {
        this.exerciseRepository = exerciseRepository;
        this.repository = repository;
        this.userRepository = userRepository;
    }

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
    private List<Exercise> prepExercises(List<ExerciseTraining> exercises) {
        if(exercises == null || exercises.isEmpty())
            return null;

        List<Exercise> exerciseToSave = new ArrayList<>(exercises.size());
        exercises.forEach(
            exerciseTraining -> {
                Exercise found = exerciseRepository.findByExercise(exerciseTraining.toExercise())
                    .orElse(exerciseTraining.toExercise());

                if(found.getId() == 0){
                    var savedExercise = exerciseRepository.save(found);
                    exerciseToSave.add(savedExercise);
                }else
                    exerciseToSave.add(found);

            }
        );
        return exerciseToSave;
    }

    public Training create(TrainingWrite toSave, String userId){
        List<Exercise> preparedExerciseList = prepExercises(toSave.getExercises());
        if(preparedExerciseList != null)
            toSave.setExercises(ExerciseTraining.toExerciseTrainingList(preparedExerciseList));

        var created = repository.save(toSave.toTraining());

        if(userId != null){
            User loggedUser = userRepository.findById(userId)
                .orElseThrow(
                    () -> new IllegalArgumentException("Nie istnieje użytkownik o takim numerze ID.")
                );
            Set<Training> oldUsersTrainings = loggedUser.getTrainings();
            oldUsersTrainings.add(created);
            loggedUser.setTrainings(oldUsersTrainings);
            userRepository.save(loggedUser);
        }

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

    private void editTrainingInUsers(Training toAddOrRemove, Set<User> toEdit, boolean ifAdd){
        if(toEdit == null)
            return;
        toEdit.forEach(
            user -> {
                if(ifAdd)
                    user.getTrainings().add(toAddOrRemove);
                else
                    user.getTrainings().remove(toAddOrRemove);
                userRepository.save(user);
            }
        );
    }

    public Page<TrainingRead> getAll(Pageable page){
        Page<TrainingRead> toReturn = repository.findAll(page).map(TrainingRead::new);
        if(toReturn.getContent().isEmpty())
            toReturn = repository.findAll(
                PageRequest.of(
                    toReturn.getTotalPages() - 2,
                    toReturn.getSize()
                )
            ).map(TrainingRead::new);

        return toReturn;
    }

    public Training getById(int id, String userId){
        Training found = repository.findById(id)
            .orElseThrow(
                () -> new IllegalArgumentException("Nie znaleziono treningu o podanym numerze id.")
            );

        if(userId == null)
            return found;

        if(
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
    public List<TrainingRead> getByUserId(String userId){
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

    public void edit(TrainingWrite toEdit, int id, String userId){
        List<Exercise> preparedExerciseList = prepExercises(toEdit.getExercises());
        toEdit.setExercises(ExerciseTraining.toExerciseTrainingList(preparedExerciseList));

        Training toSave = getById(id, userId);
        editTrainingInExercises(toSave, toSave.getExercises(), false);

        toSave.copy(toEdit.toTraining());
        var saved = repository.save(toSave);
        editTrainingInExercises(saved, saved.getExercises(), true);
    }
    public void delete(int id, String userId){
        var toDelete = getById(id, userId);
        editTrainingInExercises(toDelete, toDelete.getExercises(), false);
        editTrainingInUsers(toDelete, toDelete.getUsers(), false);

        repository.deleteById(id);
    }

}
