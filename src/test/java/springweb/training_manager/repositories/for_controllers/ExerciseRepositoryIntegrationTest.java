package springweb.training_manager.repositories.for_controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import springweb.training_manager.models.entities.BodyPart;
import springweb.training_manager.models.entities.Exercise;
import springweb.training_manager.models.entities.ExerciseParameters;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ExerciseRepositoryIntegrationTest {
    @Autowired
    private ExerciseRepository repository;
    @Autowired
    private TestEntityManager entityManager;

    @Test
    void saveInsertShouldCreateNewExercise() {
        // given
        var parameters = new ExerciseParameters(
            0,
            3,
            0,
            (short) 0,
            LocalTime.of(0, 0, 45)
        );
        var savedParameters = entityManager.persist(parameters);
        var toSave = new Exercise(
            "Test integracyjny",
            "Ćwiczenie z testu integracyjnego",
            BodyPart.UPPER_ABS,
            savedParameters,
            2
        );

        // when
        var inserted = repository.save(toSave);

        // then
        Exercise foundByEM = entityManager.find(Exercise.class, inserted.getId()); // found by entity manager
        assertEquals(
            foundByEM,
            inserted
        );
    }

    @Test
    void findByExerciseShouldFindDuplicate() {
        // given
        var parameters = new ExerciseParameters(
            0,
            3,
            0,
            (short) 0,
            LocalTime.of(0, 0, 45)
        );
        var savedParameters = entityManager.persist(parameters);
        var toSave = new Exercise(
            "Przeciąganie liny",
            "Ćw na brzuch, udawane przeciąganie liny",
            BodyPart.UPPER_ABS,
            savedParameters,
            2
        );

        // when
        entityManager.persist(toSave);
        var found = repository.findDuplication(toSave);

        //then
        assertEquals(toSave, found.get());
    }

    @Test
    void findDuplicationShouldNotFoundInDatabase() {
        // given
        var firstParams = new ExerciseParameters(
            0,
            3,
            0,
            (short) 0,
            LocalTime.of(0, 0, 45)
        );
        var savedFirstParams = entityManager.persist(firstParams);
        var toSearch = new Exercise(
            "Przeciąganie liny",
            "Ćw na brzuch, udawane przeciąganie liny",
            BodyPart.UPPER_ABS,
            savedFirstParams,
            2
        );

        var secondParams = new ExerciseParameters(
            0,
            2,
            1,
            (short) 6,
            null
        );
        var savedSecondParams = entityManager.persist(secondParams);
        var otherExercise = new Exercise(
            "Inne ćwiczenie",
            "Ćw które zostanie zwrócone w optional",
            BodyPart.HAMS,
            savedSecondParams,
            3
        );

        // when
        var found = repository.findDuplication(toSearch)
            .orElse(otherExercise);

        //then
        assertEquals(otherExercise, found);
    }
}