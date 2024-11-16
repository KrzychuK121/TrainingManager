package springweb.training_manager.repositories.for_controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import springweb.training_manager.models.entities.BodyPart;
import springweb.training_manager.models.entities.Exercise;
import springweb.training_manager.models.entities.ExerciseParameters;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ExerciseRepositoryIntegrationTest {
    @Autowired
    private ExerciseRepository repository;
    @Autowired
    private TestEntityManager entityManager;

    @Test
    void saveInsertShouldCreateNewExercise() {
        // given
        var toSave = new Exercise(
            "Test integracyjny",
            "Ćwiczenie z testu integracyjnego",
            BodyPart.UPPER_ABS,
            new ExerciseParameters(
                0,
                3,
                0,
                (short) 0,
                LocalTime.of(0, 0, 45)
            ),
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
        var toSave = new Exercise(
            "Przeciąganie liny",
            "Ćw na brzuch, udawane przeciąganie liny",
            BodyPart.UPPER_ABS,
            new ExerciseParameters(
                0,
                3,
                0,
                (short) 0,
                LocalTime.of(0, 0, 45)
            ),
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
        var toSearch = new Exercise(
            "Przeciąganie liny",
            "Ćw na brzuch, udawane przeciąganie liny",
            BodyPart.UPPER_ABS,
            new ExerciseParameters(
                0,
                3,
                0,
                (short) 0,
                LocalTime.of(0, 0, 45)
            ),
            2
        );

        var otherExercise = new Exercise(
            "Inne ćwiczenie",
            "Ćw które zostanie zwrócone w optional",
            BodyPart.HAMS,
            new ExerciseParameters(
                0,
                2,
                1,
                (short) 6,
                null
            ),
            3
        );

        // when
        var found = repository.findDuplication(toSearch)
            .orElse(otherExercise);

        //then
        assertEquals(otherExercise, found);
    }
}