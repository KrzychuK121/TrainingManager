package springweb.trainingmanager.repositories.forcontrollers;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import springweb.trainingmanager.models.entities.Training;
import springweb.trainingmanager.models.entities.TrainingSchedule;
import springweb.trainingmanager.models.entities.Weekdays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class TrainingScheduleRepositoryIntegrationTest {

    @Autowired
    private TrainingScheduleRepository repository;
    @Autowired
    private TestEntityManager entityManager;
    private Logger logger = LoggerFactory.getLogger(TrainingScheduleRepositoryIntegrationTest.class);

    @Test
    void findAll() {
        // given
        /*Training training1 = createTraining("First training", "first training");
        Training training2 = createTraining("Second training", "second training");
        Training training3 = createTraining("Third training", "third training");*/

        TrainingSchedule first = new TrainingSchedule(1, Weekdays.WEDNESDAY);
        TrainingSchedule second = new TrainingSchedule(2, Weekdays.FRIDAY);
        TrainingSchedule third = new TrainingSchedule(3, Weekdays.SATURDAY);

        // when
        /*entityManager.persist(training1);
        entityManager.persist(training2);
        entityManager.persist(training3);*/

        entityManager.persist(first);
        entityManager.persist(second);
        entityManager.persist(third);

        var found = repository.findAll();

        // then
        assertTrue(
        found.contains(first) &&
                found.contains(second) &&
                found.contains(third)
        );
    }

    /*private Training createTraining(String title, String description){
        var toReturn = new Training();

        toReturn.setTitle(title);
        toReturn.setDescription(description);

        return toReturn;
    }*/

    @Test
    public void saveInsertOperationWorks() {
        // given
        TrainingSchedule newTrainingSchedule = new TrainingSchedule(1, Weekdays.FRIDAY);

        // when
        TrainingSchedule insertedRow = repository.save(newTrainingSchedule);

        // then
        TrainingSchedule foundByEM = entityManager.find(TrainingSchedule.class, insertedRow.getId()); // found by entity manager
        assertEquals(
            foundByEM,
            insertedRow
        );

    }

    @Test
    void saveInsertOperationFailsWhenSavingDuplicatedRow(){
        /*// given
        TrainingScheduleId duplicatedId = new TrainingScheduleId(4, Weekdays.SATURDAY);

        TrainingSchedule newTrainingSchedule = new TrainingSchedule();
        newTrainingSchedule.setId(
            duplicatedId
        );

        TrainingSchedule duplicationOfNewTrainingSchedule = new TrainingSchedule();
        duplicationOfNewTrainingSchedule.setId(
            duplicatedId
        );

        // when
        TrainingSchedule saved = repository.save(newTrainingSchedule);
        repository.save(duplicationOfNewTrainingSchedule);

        entityManager.remove(saved);

        // then
        assertNull(entityManager.find(TrainingSchedule.class, newTrainingSchedule.getId()));*/
    }

    @Test
    void saveUpdateOperationWorks(){

    }

    @Test
    void findById() {

    }

    @Test
    void existsById() {

    }

    @Test
    void delete() {

    }
}