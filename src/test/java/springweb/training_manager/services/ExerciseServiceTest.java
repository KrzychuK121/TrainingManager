package springweb.training_manager.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import springweb.training_manager.exceptions.NotOwnedByUserException;
import springweb.training_manager.models.entities.*;
import springweb.training_manager.models.schemas.RoleSchema;
import springweb.training_manager.models.viewmodels.exercise.ExerciseWrite;
import springweb.training_manager.models.viewmodels.exercise_parameters.ExerciseParametersRead;
import springweb.training_manager.models.viewmodels.training.TrainingExerciseVM;
import springweb.training_manager.repositories.for_controllers.ExerciseRepository;
import springweb.training_manager.repositories.for_controllers.TrainingRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ExerciseServiceTest {
    private ExerciseService exerciseService;

    private ExerciseRepository repository;
    private ExerciseParametersService parametersService;
    private TrainingExerciseService trainingExerciseService;
    private TrainingRepository trainingRepository;
    private TrainingService trainingService;


    @BeforeEach
    void setup() {
        repository = mock(ExerciseRepository.class);
        parametersService = mock(ExerciseParametersService.class);
        trainingExerciseService = mock(TrainingExerciseService.class);
        trainingRepository = mock(TrainingRepository.class);
        trainingService = mock(TrainingService.class);

        exerciseService = spy(
            new ExerciseService(
                repository,
                parametersService,
                trainingExerciseService,
                trainingRepository,
                trainingService
            )
        );
    }

    User getUser(String name) {
        User user = new User(
            name,
            name,
            name + name,
            "foopassword"
        );

        user.setRoles(
            Set.of(
                new Role(RoleSchema.ROLE_USER)
            )
        );

        return user;
    }

    User getAdmin() {
        User admin = new User(
            "admin",
            "admin",
            "adminadmin",
            "foopassword"
        );

        admin.setRoles(
            Set.of(
                new Role(RoleSchema.ROLE_ADMIN)
            )
        );

        return admin;
    }

    Training getTraining(
        Training initData,
        User owner
    ) {
        initData.setOwner(owner);
        return initData;
    }

    @Test
    void prepTrainings_returns_null_for_null_trainings() {
        // Given

        // When
        var prepared = exerciseService.prepTrainings(
            null,
            null
        );

        // Then
        assertNull(prepared, "Prepared list should be null.");
        verify(trainingRepository, never()).save(any());
        verify(trainingRepository, never()).findDuplication(any());
    }

    /**
     * This method has to contain ONLY 3 elements where:
     * - one is owned by <code>user1</code>
     * - one is owned by <code>user2</code>
     * - one is public (has no user)
     *
     * @param user1 first owner, different from <code>user2</code>
     * @param user2 second owner, different from <code>user1</code>
     *
     * @return list of 3 trainings
     */
    List<Training> getOwnedByDiffUsersAndPublicTrainings(
        User user1,
        User user2
    ) {
        return List.of(
            getTraining(
                new Training("training1", "fooDesc"),
                user1
            ),
            getTraining(
                new Training("training2", "anotherFooDesc"),
                user2
            ),
            new Training("training3", "publicTraining")
        );
    }

    MockedStatic<NoDuplicationService> getNoDuplicationMock(List<Training> toReturnByMock) {
        var mock = mockStatic(NoDuplicationService.class);
        mock.when(
                () -> NoDuplicationService.prepEntitiesWithWriteModel(
                    anyList(),
                    any(),
                    any()
                )
            )
            .thenReturn(toReturnByMock);

        return mock;
    }

    @Test
    void prepTrainings_returns_all_for_admin() {
        // Given
        var user1 = getUser("user1");
        var user2 = getUser("user2");
        var loggedUser = getAdmin();

        var allTrainingList = getOwnedByDiffUsersAndPublicTrainings(
            user1,
            user2
        );
        var originalSize = allTrainingList.size();

        try (var mockedNoDuplicationCall = getNoDuplicationMock(allTrainingList)) {
            // When
            var retrieved = exerciseService.prepTrainings(
                List.of(),
                loggedUser
            );

            // Then
            assertEquals(originalSize, retrieved.size());
        }
    }

    @Test
    void prepTrainings_returns_only_private_and_public_trainings_for_user() {
        // Given
        var user1 = getUser("user1");
        var user2 = getUser("user2");
        var loggedUser = user1;

        var allTrainingList = getOwnedByDiffUsersAndPublicTrainings(
            user1,
            user2
        );
        var expectedSize = 2;

        try (var mockedNoDuplicationCall = getNoDuplicationMock(allTrainingList)) {
            // When
            var retrieved = exerciseService.prepTrainings(
                List.of(),
                loggedUser
            );

            // Then
            assertEquals(expectedSize, retrieved.size());
        }

    }

    @Test
    void prepTrainings_returns_only_public_trainings_for_user_without_private_trainings() {
        // Given
        var user1 = getUser("user1");
        var user2 = getUser("user2");
        var loggedUser = getUser("user3");

        var allTrainingList = getOwnedByDiffUsersAndPublicTrainings(
            user1,
            user2
        );

        var expectedSize = 1;

        try (var mockedNoDuplicationCall = getNoDuplicationMock(allTrainingList)) {
            // When
            var retrieved = exerciseService.prepTrainings(
                List.of(),
                loggedUser
            );

            // Then
            assertEquals(expectedSize, retrieved.size());
        }
    }

    @Test
    void setTrainingsById_assigns_new_training_when_previous_null() {
        // Given
        var toSave = new ExerciseWrite();
        toSave.setTrainings(null);
        var fooTraining = new Training();
        var trainingIds = new String[]{"1", "2", "3"};
        var predictedSize = trainingIds.length;

        when(trainingRepository.findById(anyInt()))
            .thenReturn(
                Optional.of(fooTraining)
            );

        try (var mockUserServiceCall = mockStatic(UserService.class)) {
            mockUserServiceCall.when(
                    () -> UserService.isPermittedFor(any(), any())
                )
                .thenReturn(true);

            // When
            exerciseService.setTrainingsById(
                toSave,
                trainingIds,
                new User()
            );
            var newSize = toSave.getTrainings()
                .size();

            // Then
            assertNotNull(toSave.getTrainings());
            assertFalse(
                toSave.getTrainings()
                    .isEmpty()
            );
            assertEquals(
                predictedSize,
                newSize,
                String.format(
                    "New size (%d) not matching predicted size (%d).",
                    newSize,
                    predictedSize
                )
            );
        }
    }

    @Test
    void setTrainingsById_adds_new_to_old_trainings() {
        // Given
        var toSave = new ExerciseWrite();
        toSave.setTrainings(
            List.of(
                new TrainingExerciseVM(),
                new TrainingExerciseVM()
            )
        );
        var trainingIds = new String[]{"0", "1"};
        var predictedSize = toSave.getTrainings()
            .size() + trainingIds.length;

        var fooTraining = new Training();
        fooTraining.setOwner(new User());

        when(trainingRepository.findById(anyInt()))
            .thenReturn(Optional.of(fooTraining));

        try (var mockUserServiceCall = mockStatic(UserService.class)) {
            mockUserServiceCall.when(
                    () -> UserService.isPermittedFor(any(), any())
                )
                .thenReturn(true);

            // When
            exerciseService.setTrainingsById(
                toSave,
                trainingIds,
                new User()
            );

            var newSize = toSave.getTrainings()
                .size();

            // Then
            assertNotNull(toSave.getTrainings());
            assertFalse(
                toSave.getTrainings()
                    .isEmpty()
            );
            assertEquals(
                predictedSize,
                newSize,
                String.format(
                    "New size (%d) not matching predicted size (%d).",
                    newSize,
                    predictedSize
                )
            );
        }
    }

    @Test
    void setTrainingsById_ignores_not_found_training() {
        // Given
        var toSave = new ExerciseWrite();
        var trainingIds = new String[]{"0", "1", "3", "2"};

        var allTrainings = getOwnedByDiffUsersAndPublicTrainings(
            new User(),
            new User()
        );

        var predictedSize = allTrainings.size();

        for (var trainingId : trainingIds) {
            var trainingIdInt = Integer.parseInt(trainingId);

            Optional<Training> optionalToReturn = trainingIdInt < allTrainings.size()
                ? Optional.of(allTrainings.get(trainingIdInt))
                : Optional.empty();
            when(trainingRepository.findById(trainingIdInt))
                .thenReturn(optionalToReturn);
        }

        try (var mockUserServiceCall = mockStatic(UserService.class)) {
            mockUserServiceCall.when(
                    () -> UserService.isPermittedFor(any(), any())
                )
                .thenReturn(true);

            // When
            exerciseService.setTrainingsById(
                toSave,
                trainingIds,
                new User()
            );
            var newSize = toSave.getTrainings()
                .size();

            // Then
            assertNotNull(toSave.getTrainings());
            assertFalse(
                toSave.getTrainings()
                    .isEmpty()
            );
            assertEquals(
                predictedSize,
                newSize,
                String.format(
                    "New size (%d) not matching predicted size (%d).",
                    newSize,
                    predictedSize
                )
            );

            for (int i = 0; i < allTrainings.size(); i++) {
                var validTraining = allTrainings.get(i);
                var toSaveTraining = toSave.getTrainings()
                    .get(i);

                assertEquals(validTraining.getId(), toSaveTraining.getId());
                assertEquals(validTraining.getTitle(), toSaveTraining.getTitle());
                assertEquals(validTraining.getDescription(), toSaveTraining.getDescription());
            }
        }
    }

    @Test
    void setTrainingsById_returns_all_resources_to_administrator() {
        // Given
        var toSave = new ExerciseWrite();
        toSave.setTrainings(List.of());

        var user1 = getUser("user1");
        var user2 = getUser("user2");
        var admin = getAdmin();

        var allTrainings = getOwnedByDiffUsersAndPublicTrainings(
            user1,
            user2
        );
        var trainingIds = new String[allTrainings.size()];

        var oldTrainingsSize = toSave.getTrainings()
            .size();
        var predictedSize = oldTrainingsSize + allTrainings.size();

        for (int i = 0; i < allTrainings.size(); i++) {
            trainingIds[i] = String.valueOf(i);
            when(trainingRepository.findById(i))
                .thenReturn(
                    Optional.of(
                        allTrainings.get(i)
                    )
                );
        }

        // When
        exerciseService.setTrainingsById(
            toSave,
            trainingIds,
            admin
        );
        var newSize = toSave.getTrainings()
            .size();

        // Then
        assertEquals(
            predictedSize,
            newSize,
            String.format(
                "New size (%d) not matching predicted size (%d).",
                newSize,
                predictedSize
            )
        );

        for (int i = 0; i < allTrainings.size(); i++) {
            var validTraining = allTrainings.get(i);
            var toSaveTraining = toSave.getTrainings()
                .get(i);

            assertEquals(validTraining.getId(), toSaveTraining.getId());
            assertEquals(validTraining.getTitle(), toSaveTraining.getTitle());
            assertEquals(validTraining.getDescription(), toSaveTraining.getDescription());
        }
    }

    @Test
    void setTrainingsById_returns_private_and_public_resources_to_default_user() {
        // Given
        var toSave = new ExerciseWrite();
        toSave.setTrainings(List.of());

        var user1 = getUser("user1");
        var user2 = getUser("user2");
        var requestingUser = user1;

        var allTrainings = getOwnedByDiffUsersAndPublicTrainings(
            user1,
            user2
        );
        var trainingIds = new String[allTrainings.size()];

        var predictedSize = 2;

        for (int i = 0; i < allTrainings.size(); i++) {
            trainingIds[i] = String.valueOf(i);
            when(trainingRepository.findById(i))
                .thenReturn(
                    Optional.of(
                        allTrainings.get(i)
                    )
                );
        }

        // When
        exerciseService.setTrainingsById(
            toSave,
            trainingIds,
            requestingUser
        );
        var newSize = toSave.getTrainings()
            .size();

        // Then
        assertEquals(
            predictedSize,
            newSize,
            String.format(
                "New size (%d) not matching predicted size (%d).",
                newSize,
                predictedSize
            )
        );

        for (var retrievedTraining : toSave.getTrainings()) {
            var exists = allTrainings.stream()
                .anyMatch(
                    training -> (
                        training.getId()
                            .equals(retrievedTraining.getId())
                            && training.getOwner()
                            .getUsername()
                            .equals(requestingUser.getUsername())
                    )
                );
            assertTrue(exists);
        }
    }

    @Test
    void create() {
        // TODO: Implement me pls
        assertTrue(false);
    }

    @Test
    void getById_throws_IllegalArgumentException_because_entity_does_not_exist() {
        // Given
        when(repository.findById(any()))
            .thenReturn(Optional.empty());

        // When
        var exception = catchThrowable(
            () -> exerciseService.getById(0, new User())
        );

        // Then
        assertThat(exception)
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("does not exist");
    }

    @Test
    void getById_returns_by_id_to_admin() {
        // Given
        var admin = getAdmin();
        var owner = getUser("owner");

        var fooExercise = new Exercise();
        fooExercise.setOwner(owner);

        when(repository.findById(any()))
            .thenReturn(Optional.of(fooExercise));

        // When
        var found = exerciseService.getById(0, admin);

        // Then
        assertEquals(
            fooExercise,
            found,
            String.format(
                "Found with (name: %s) does not match with fooExercise (name: %s)",
                found.getName(),
                fooExercise.getName()
            )
        );

    }

    @Test
    void getById_returns_by_id_to_owner() {
        // Given
        var owner = getUser("owner");
        var requesting = owner;

        var fooExercise = new Exercise();
        fooExercise.setOwner(owner);

        when(repository.findById(any()))
            .thenReturn(Optional.of(fooExercise));

        // When
        var found = exerciseService.getById(0, requesting);

        // Then
        assertEquals(
            fooExercise,
            found,
            String.format(
                "Found with (name: %s) does not match with fooExercise (name: %s)",
                found.getName(),
                fooExercise.getName()
            )
        );

    }

    @Test
    @DisplayName(
        "getById throws NotOwnedByUserExercise because " +
            "loggedUser is not an owner of requested entity"
    )
    void getById_throws_NotOwnedByUserException_because_loggedUser_is_not_an_owner() {
        // Given
        var requestingUser = getUser("user1");
        var owner = getUser("user2");

        var fooExercise = new Exercise();
        fooExercise.setOwner(owner);

        when(repository.findById(any()))
            .thenReturn(
                Optional.of(fooExercise)
            );

        // When
        var exception = catchThrowable(
            () -> exerciseService.getById(0, requestingUser)
        );

        // Then
        assertThat(exception)
            .isInstanceOf(NotOwnedByUserException.class)
            .hasMessageContaining("is not an owner of exercise with id");
    }

    @Test
    void edit() {
    }

    @Test
    void delete_does_not_delete_when_not_retrieving_exercise_by_id() {
        // Given
        var message = "test message";
        when(repository.findById(anyInt()))
            .thenThrow(new IllegalArgumentException(message));
        // When
        var exception = catchThrowable(
            () -> exerciseService.delete(0, new User())
        );

        // Then
        assertThat(exception).isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining(message);
        verify(trainingExerciseService, never()).deleteByExerciseId(any());
        verify(repository, never()).deleteById(any());
        verify(parametersService, never()).deleteIfOrphaned(any(ExerciseParametersRead.class));
    }

    private static ArrayList<TrainingExercise> getConnectionsToRemove(
        String titlePrefix,
        String descPrefix,
        Exercise fooExercise
    ) {
        Function<Integer, Training> getTrainingByNumber = number -> new Training(
            titlePrefix + number,
            descPrefix + number
        );
        var otherExercise = new Exercise();
        otherExercise.setName("otherExercise");

        return new ArrayList<>(
            List.of(
                new TrainingExercise(
                    getTrainingByNumber.apply(1),
                    fooExercise,
                    new ExerciseParameters()
                ),
                new TrainingExercise(
                    getTrainingByNumber.apply(2),
                    fooExercise,
                    new ExerciseParameters()
                ),
                new TrainingExercise(
                    getTrainingByNumber.apply(3),
                    otherExercise,
                    new ExerciseParameters()
                )
            )
        );
    }

    @Test
    void delete_removes_training_exercise_connections() {
        // Given
        final var EXERCISE_NAME = "fooExercise";
        var fooExercise = new Exercise();
        fooExercise.setName(EXERCISE_NAME);
        fooExercise.setParameters(new ExerciseParameters());

        final var TITLE_PREFIX = "fooTitle";
        final var DESC_PREFIX = "description";
        var connectionsToRemove = getConnectionsToRemove(
            TITLE_PREFIX,
            DESC_PREFIX,
            fooExercise
        );

        var removed = new ArrayList<TrainingExercise>();
        var predictedRemovedSize = 2;

        doReturn(fooExercise)
            .when(exerciseService)
            .getById(anyInt(), any());
        doNothing()
            .when(repository)
            .deleteById(anyInt());
        doAnswer(
            answer -> {
                Predicate<TrainingExercise> findCondition = trainingExercise -> trainingExercise.getExercise()
                    .getName()
                    .equals(EXERCISE_NAME);

                removed.addAll(
                    connectionsToRemove.stream()
                        .filter(findCondition)
                        .toList()
                );
                connectionsToRemove.removeIf(findCondition);
                return null;
            }
        ).when(trainingExerciseService)
            .deleteByExerciseId(any(Exercise.class));
        doNothing()
            .when(parametersService)
            .deleteIfOrphaned(any(ExerciseParametersRead.class));

        // When
        exerciseService.delete(0, new User());

        // Then
        assertEquals(
            predictedRemovedSize,
            removed.size(),
            String.format(
                "Predicted size of removed elements(%s) differ from actual size(%s)",
                predictedRemovedSize,
                removed.size()
            )
        );
        for (int i = 1; i < 3; i++) {
            final int finalI = i;
            assertTrue(
                removed.stream()
                    .anyMatch(
                        trainingExercise -> {
                            var training = trainingExercise.getTraining();
                            return training.getTitle()
                                .equals(TITLE_PREFIX + finalI)
                                && training.getDescription()
                                .equals(DESC_PREFIX + finalI);
                        }
                    )
            );
        }
        verify(trainingExerciseService, times(1))
            .deleteByExerciseId(any());
    }

    @Test
    void delete_removes_orphaned_exercise_parameters() {
        // Given
        var fooExercise = new Exercise();
        fooExercise.setParameters(new ExerciseParameters());
        doReturn(fooExercise)
            .when(exerciseService)
            .getById(anyInt(), any());
        doNothing()
            .when(repository)
            .deleteById(anyInt());
        doNothing()
            .when(trainingExerciseService)
            .deleteByExerciseId(any(Exercise.class));
        doNothing()
            .when(parametersService)
            .deleteIfOrphaned(any(ExerciseParametersRead.class));

        // When
        exerciseService.delete(0, new User());

        // Then
        verify(parametersService, times(1)).deleteIfOrphaned(
            any(ExerciseParametersRead.class)
        );
    }
}
