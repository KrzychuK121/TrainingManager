package springweb.training_manager.repositories.for_controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import springweb.training_manager.models.entities.*;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TrainingPlanRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private TrainingPlanRepository repository;

    @Test
    void findAllActivePlansForWeekdayAndTrainingTimeBetween() {
        // Given
        final var TEST_WEEKDAY = Weekdays.FRIDAY;
        final var OTHER_WEEKDAY = Weekdays.SATURDAY;
        final var TIME_BEFORE = LocalTime.of(10, 0);
        final var TIME_AFTER = LocalTime.of(11, 0);

        assertNotEquals(
            TEST_WEEKDAY,
            OTHER_WEEKDAY,
            "Test weekday can't be the same as other weekday"
        );

        final var TEST_DAY_SCHEDULES_COUNT = 5;

        var user = new User(
            "usernameTestFoo",
            "fooLastname",
            "fooFirstname",
            "passwordFoo12!"
        );
        user.setPasswordHashed("passwordFoo12!");
        user = entityManager.persist(user);
        user.setRole(Role.USER);

        var training = new Training(
            "fooTitle",
            "fooDescription"
        );

        entityManager.persist(training);
        training.setOwner(user);


        var testDaySchedule = new TrainingSchedule(
            training.getId(),
            TEST_WEEKDAY
        );
        testDaySchedule.setTraining(training);
        entityManager.persist(testDaySchedule);

        var otherDaySchedule = new TrainingSchedule(
            training.getId(),
            OTHER_WEEKDAY
        );
        otherDaySchedule.setTraining(training);
        entityManager.persist(otherDaySchedule);

        for (int i = 0; i < TEST_DAY_SCHEDULES_COUNT; i++) {
            var routine = new TrainingRoutine();
            routine.setActive(true);
            routine.setOwner(user);
            entityManager.persist(routine);
            entityManager.persist(
                new TrainingPlan(
                    routine,
                    testDaySchedule,
                    LocalTime.of(
                        TIME_BEFORE.getHour(),
                        5 * i,
                        2 * i
                    )
                )
            );
        }

        var notActiveRoutine = new TrainingRoutine();
        notActiveRoutine.setActive(false);
        notActiveRoutine.setOwner(user);
        entityManager.persist(notActiveRoutine);

        entityManager.persist(
            new TrainingPlan(
                notActiveRoutine,
                testDaySchedule,
                LocalTime.of(
                    TIME_BEFORE.getHour(),
                    50
                )
            )
        );

        var anotherActiveRoutine = new TrainingRoutine();
        anotherActiveRoutine.setActive(true);
        anotherActiveRoutine.setOwner(user);
        entityManager.persist(anotherActiveRoutine);

        entityManager.persist(
            new TrainingPlan(
                anotherActiveRoutine,
                otherDaySchedule,
                LocalTime.of(
                    TIME_BEFORE.getHour(),
                    25
                )
            )
        );

        // When
        var found = repository.findAllActivePlansForWeekdayAndTrainingTimeBetween(
            TEST_WEEKDAY,
            TIME_BEFORE,
            TIME_AFTER
        );

        // Then
        assertEquals(
            TEST_DAY_SCHEDULES_COUNT,
            found.size()
        );
    }
}