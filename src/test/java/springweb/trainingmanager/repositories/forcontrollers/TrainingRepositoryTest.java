package springweb.trainingmanager.repositories.forcontrollers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import springweb.trainingmanager.models.entities.Training;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TrainingRepositoryTest {

    @Autowired
    private TrainingRepository repository;
    @Autowired
    private TestEntityManager entityManager;

    @Test
    void saveInsertOperationWorks() {
        // given
        Training toSave = new Training();
        toSave.setTitle("Test integracyjny");
        toSave.setDescription("Trening do testu integracyjnego");

        // when
        Training insertedRow = repository.save(toSave);

        // then
        Training foundByEM = entityManager.find(Training.class, insertedRow.getId()); // found by entity manager
        assertEquals(
            foundByEM,
            insertedRow
        );
    }
}