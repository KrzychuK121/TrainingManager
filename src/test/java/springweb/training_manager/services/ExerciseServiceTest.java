package springweb.training_manager.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import springweb.training_manager.models.entities.Role;
import springweb.training_manager.models.entities.Training;
import springweb.training_manager.models.entities.User;
import springweb.training_manager.models.schemas.RoleSchema;
import springweb.training_manager.repositories.for_controllers.ExerciseRepository;
import springweb.training_manager.repositories.for_controllers.TrainingRepository;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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

        exerciseService = new ExerciseService(
            repository,
            parametersService,
            trainingExerciseService,
            trainingRepository,
            trainingService
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
    void setTrainingsById() {
        
    }

    @Test
    void create() {
    }

    @Test
    void getById() {
    }

    @Test
    void edit() {
    }

    @Test
    void delete() {
    }
}
