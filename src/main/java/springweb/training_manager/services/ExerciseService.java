package springweb.training_manager.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import springweb.training_manager.exceptions.NotOwnedByUserException;
import springweb.training_manager.models.entities.Exercise;
import springweb.training_manager.models.entities.Role;
import springweb.training_manager.models.entities.Training;
import springweb.training_manager.models.entities.User;
import springweb.training_manager.models.viewmodels.exercise.*;
import springweb.training_manager.models.viewmodels.exercise_parameters.ExerciseParametersRead;
import springweb.training_manager.models.viewmodels.training.TrainingExerciseVM;
import springweb.training_manager.models.viewmodels.validation.ValidationErrors;
import springweb.training_manager.repositories.for_controllers.ExerciseRepository;
import springweb.training_manager.repositories.for_controllers.TrainingRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

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
     * <code>TrainingExerciseVM</code> (founded in database or just created)
     */
    public List<Training> prepTrainings(
        List<TrainingExerciseVM> trainings,
        User loggedUser
    ) {
        List<Training> trainingsToSave = NoDuplicationService.prepEntitiesWithWriteModel(
            trainings,
            trainingRepository,
            trainingRepository::save
        );

        if (trainingsToSave == null)
            return null;

        var filteredTrainings = trainingsToSave.stream()
            .filter(
                training -> UserService.isPermittedToReadFor(
                    loggedUser,
                    training.getOwner()
                )
            )
            .toList();
        return filteredTrainings.isEmpty() ? null : filteredTrainings;
    }

    /**
     * This method fetches trainings objects based on provided training ids. It expands
     * previous table with fetched trainings instead of replacing.
     *
     * @param toSave      write model to set fetched trainings
     * @param trainingIds ids of trainings to fetch
     */
    public void setTrainingsById(
        ExerciseWrite toSave,
        String[] trainingIds,
        User user
    ) {
        if (trainingIds == null || trainingIds.length == 0)
            return;

        List<TrainingExerciseVM> trainingsToSave = new ArrayList<>(trainingIds.length);
        if (toSave.getTrainings() != null)
            trainingsToSave.addAll(toSave.getTrainings());
        for (String trainingID : trainingIds) {
            if (trainingID.isEmpty())
                continue;
            int id = Integer.parseInt(trainingID);
            Training found = trainingRepository.findById(id)
                .orElse(null);
            if (
                found == null
                    || !UserService.isPermittedToReadFor(user, found.getOwner())
            )
                continue;
            TrainingExerciseVM viewModel = new TrainingExerciseVM(found);
            trainingsToSave.add(viewModel);
        }

        toSave.setTrainings(trainingsToSave);
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

    public static int getTotalBurnedKcal(ExerciseRead exercise) {
        if (exercise == null)
            return 0;

        var parametersRead = new ExerciseParametersRead(
            exercise.getParametersId(),
            exercise.getRounds(),
            exercise.getRepetition(),
            exercise.getWeights(),
            exercise.getTime()
        );

        return ExerciseParametersService.calcTotalBurnedKcal(
            exercise.getDefaultBurnedKcal(),
            parametersRead
        );
    }

    public static int getTotalBurnedKcal(ExerciseTraining exercise) {
        if (exercise == null)
            return 0;

        var parametersRead = new ExerciseParametersRead(
            exercise.getExerciseParameters()
        );
        return ExerciseParametersService.calcTotalBurnedKcal(
            exercise.getDefaultBurnedKcal(),
            parametersRead
        );
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
        BindingResult result,
        User user
    ) {
        final var ENTITY_PREFIX = "toSave.";
        var toSave = data.getToSave();

        if (result.hasErrors()) {
            var validation = ValidationErrors.createFrom(result, ENTITY_PREFIX);
            return validation.getErrors();
        }

        setTrainingsById(
            toSave,
            data.getSelectedTrainings(),
            user
        );
        setTime(toSave);
        return null;
    }

    @Transactional
    public Exercise create(
        ExerciseWrite toSave,
        User loggedUser
    ) {
        var preparedParameters = parametersService.prepExerciseParameters(
            toSave.getParameters()
        );
        List<Training> preparedTrainingList = prepTrainings(
            toSave.getTrainings(),
            loggedUser
        );

        var entityToSave = toSave.toEntity();
        entityToSave.setParameters(preparedParameters);
        if (
            toSave.isExercisePrivate()
                && !UserService.userIsInRole(loggedUser, Role.ADMIN)
        )
            entityToSave.setOwner(loggedUser);

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

    private Page<ExerciseRead> getPageBy(
        Pageable page,
        Function<Pageable, Page<Exercise>> find
    ) {
        return PageSortService.getPageBy(
            Exercise.class,
            page,
            find,
            ExerciseRead::new,
            logger
        );
    }

    public Page<ExerciseRead> getPagedAll(Pageable page) {
        return getPageBy(page, repository::findAll);
    }

    public Page<ExerciseRead> getPagedPublicOrOwnedBy(
        Pageable page,
        User owner
    ) {
        return getPageBy(
            page,
            pageable -> repository.findPublicOrOwnedBy(
                owner.getId(),
                pageable
            )
        );
    }

    public Page<ExerciseRead> getPagedForUser(
        Pageable page,
        User owner
    ) {
        return UserService.userIsInRole(owner, Role.ADMIN)
            ? getPagedAll(page)
            : getPagedPublicOrOwnedBy(page, owner);
    }

    public Exercise getById(int id) {
        return repository.findById(id)
            .orElseThrow(
                () -> new IllegalArgumentException(
                    "Exercise with provided id(" + id + ") does not exist."
                )
            );
    }

    public Exercise getByIdForUse(
        int id,
        User loggedUser
    ) {
        var found = getById(id);
        if (!UserService.isPermittedToReadFor(loggedUser, found.getOwner()))
            throw new NotOwnedByUserException(
                "This user(" + loggedUser.getUsername() + ") can't access " +
                    "exercise with id(" + id + ") which is private."
            );

        return found;
    }

    public Exercise getByIdForModify(
        int id,
        User loggedUser
    ) {
        var found = getById(id);
        if (!UserService.isPermittedToModifyFor(loggedUser, found.getOwner()))
            throw new NotOwnedByUserException(
                "This user(" + loggedUser.getUsername() + ") is not " +
                    "an owner of exercise with id(" + id + ")."
            );
        return found;
    }

    public ExerciseCreate getCreateModel(
        Integer id,
        User owner
    ) {
        var allTrainings = trainingService.getAllForUser(owner);
        if (id == null)
            return new ExerciseCreate(allTrainings);
        try {
            return new ExerciseCreate(
                new ExerciseRead(
                    getByIdForModify(id, owner)
                ),
                allTrainings
            );
        } catch (IllegalArgumentException ex) {
            return new ExerciseCreate(allTrainings);
        }
    }

    @Transactional
    public void edit(
        ExerciseWrite toEdit,
        int id,
        User loggedUser
    ) {
        Exercise toSave = getByIdForModify(id, loggedUser);
        var oldOwner = toSave.getOwner();
        var oldParametersRead = new ExerciseParametersRead(
            toSave.getParameters()
        );

        List<Training> preparedTrainingList = prepTrainings(
            toEdit.getTrainings(),
            loggedUser
        );
        var preparedParameters = parametersService.prepExerciseParameters(
            toEdit.getParameters()
        );

        toSave.copy(toEdit.toEntity());
        if (toEdit.isExercisePrivate() && oldOwner != null)
            toSave.setOwner(oldOwner);
        toSave.setTrainingExercises(null);
        toSave.setParameters(preparedParameters);

        var saved = repository.save(toSave);
        trainingExerciseService.updateTrainingExerciseConnection(
            saved,
            preparedTrainingList
        );
        parametersService.deleteIfOrphaned(oldParametersRead);
    }

    @Transactional
    public void delete(
        int id,
        User loggedUser
    ) {
        var toDelete = getByIdForModify(id, loggedUser);
        var oldParametersRead = new ExerciseParametersRead(
            toDelete.getParameters()
        );
        trainingExerciseService.deleteByExerciseId(toDelete);
        repository.deleteById(id);
        parametersService.deleteIfOrphaned(oldParametersRead);
    }
}
