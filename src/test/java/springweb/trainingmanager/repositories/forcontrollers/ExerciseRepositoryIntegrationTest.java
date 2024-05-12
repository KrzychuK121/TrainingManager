package springweb.trainingmanager.repositories.forcontrollers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import springweb.trainingmanager.models.entities.*;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ExerciseRepositoryIntegrationTest {
    @Autowired
    private ExerciseRepository repository;
    @Autowired
    private TestEntityManager entityManager;

    @Test
    void saveInsertShouldCreateNewExercise(){
        // given
        var toSave = new Exercise(
            "Test integracyjny",
            "Ćwiczenie z testu integracyjnego",
            3,
            0,
            0,
            LocalTime.of(0, 0, 45),
            BodyPart.UPPER_ABS,
            Difficulty.MEDIUM
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
    void findByExerciseShouldFindDuplicate(){
        // given
        var toSave = new Exercise(
            "Przeciąganie liny",
            "Ćw na brzuch, udawane przeciąganie liny",
            3,
            0,
            0,
            LocalTime.of(0, 0, 45),
            BodyPart.UPPER_ABS,
            Difficulty.MEDIUM
        );

        // when
        entityManager.persist(toSave);
        var found = repository.findByExercise(toSave);

        //then
        assertEquals(toSave, found.get());
    }

    @Test
    void findByExerciseShouldNotFoundInDatabase(){
        // given
        var toSearch = new Exercise(
            "Przeciąganie liny",
            "Ćw na brzuch, udawane przeciąganie liny",
            3,
            0,
            0,
            LocalTime.of(0, 0, 45),
            BodyPart.UPPER_ABS,
            Difficulty.MEDIUM
        );

        var otherExercise = new Exercise(
            "Inne ćwiczenie",
            "Ćw które zostanie zwrócone w optional",
            2,
            1,
            6,
            null,
            BodyPart.HAMS,
            Difficulty.FOR_BEGINNERS
        );

        // when
        var found = repository.findByExercise(toSearch)
            .orElse(otherExercise);

        //then
        assertEquals(otherExercise, found);
    }
}