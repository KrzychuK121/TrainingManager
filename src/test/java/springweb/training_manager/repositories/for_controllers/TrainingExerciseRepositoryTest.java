package springweb.training_manager.repositories.for_controllers;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import springweb.training_manager.models.entities.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Slf4j
class TrainingExerciseRepositoryTest {
    @Autowired
    private TrainingExerciseRepository repository;
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private TestEntityManager testEntityManager;

    int initDatabaseForExistsByTrainingIdAndExerciseOwnerIsNotNullTests(
        int privateCounter
    ) {
        Training training = new Training();
        training.setTitle("Test training");
        training.setDescription("Foo description");
        var savedTraining = entityManager.persist(training);

        var owner = new User();
        owner.setUsername("foo owner");
        owner.setFirstName("fooName");
        owner.setLastName("fooLastName");
        var savedOwner = entityManager.persist(owner);

        var parameters = new ExerciseParameters(
            0,
            2,
            10,
            (short) 5,
            null
        );
        var savedParameters = entityManager.persist(parameters);

        final var EXERCISES_SIZE = 5;

        assertTrue(
            EXERCISES_SIZE >= privateCounter,
            String.format(
                "The EXERCISE_SIZE(%d) is lower than privateCounter(%d)",
                EXERCISES_SIZE,
                privateCounter
            )
        );

        for (int i = 0; i < EXERCISES_SIZE; i++) {
            var exercise = new Exercise();
            exercise.setName("Exercise " + i);
            exercise.setDescription("Foo description " + i);
            exercise.setBodyPart(BodyPart.TRAPS);
            exercise.setParameters(savedParameters);

            if (privateCounter-- > 0)
                exercise.setOwner(savedOwner);

            var savedExercise = entityManager.persist(exercise);

            entityManager.persist(
                new TrainingExercise(
                    savedTraining,
                    savedExercise,
                    savedParameters
                )
            );
        }

        return savedTraining.getId();
    }

    @Test
    void existsByTrainingIdAndExerciseOwnerIsNotNull_finds_private_exercise() {
        // Given
        var privateCounter = 1;
        var savedTrainingId = initDatabaseForExistsByTrainingIdAndExerciseOwnerIsNotNullTests(privateCounter);

        // When
        var response = repository.existsByTrainingIdAndExerciseOwnerIsNotNull(savedTrainingId);

        // Then
        assertTrue(
            response,
            String.format("There is %s exercise with user and query should find it.", privateCounter)
        );
    }

    @Test
    void existsByTrainingIdAndExerciseOwnerIsNotNull_finds_many_private_exercises_for_training() {
        // Given
        var privateCounter = 2;
        var savedTrainingId = initDatabaseForExistsByTrainingIdAndExerciseOwnerIsNotNullTests(privateCounter);

        // When
        var response = repository.existsByTrainingIdAndExerciseOwnerIsNotNull(savedTrainingId);

        // Then
        assertTrue(
            response,
            String.format(
                "There is %s exercises with user and query should find them.",
                privateCounter
            )
        );
    }

    @Test
    void existsByTrainingIdAndExerciseOwnerIsNotNull_finds_no_private_exercise() {
        // Given
        var privateCounter = 0;
        var savedTrainingId = initDatabaseForExistsByTrainingIdAndExerciseOwnerIsNotNullTests(privateCounter);

        // When
        var response = repository.existsByTrainingIdAndExerciseOwnerIsNotNull(savedTrainingId);

        // Then
        assertFalse(
            response,
            "There is no private (associated with user) exercise. " +
                "Make sure privateCounter is equal to 0"
        );
    }

    @Test
    void existsByTrainingIdAndExerciseOwnerIsNotNull_finds_no_private_exercise_when_training_does_not_exist() {
        // Given
        final var PRIMARY_KEY = 1000;
        var found = testEntityManager.find(Training.class, PRIMARY_KEY);

        if (found != null)
            testEntityManager.remove(found);

        // When
        var response = repository.existsByTrainingIdAndExerciseOwnerIsNotNull(PRIMARY_KEY);

        // Then
        assertFalse(
            response,
            "The training does not exists so there should not be any " +
                "private exercises associated with him"
        );
    }
}