package springweb.trainingmanager.repositories.forcontrollers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import springweb.trainingmanager.models.entities.User;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository repository;
    @Autowired
    private TestEntityManager entityManager;

    @Test
    void saveInsertShouldWork() {
        // given
        User toSave = new User();
        toSave.setFirstName("testowy");
        toSave.setLastName("testowy");
        toSave.setUsername("testowynickname");
        toSave.setPasswordHashed("testowehaslo");

        // when
        var insertedRow = repository.save(toSave);
        var found = entityManager.find(User.class, toSave.getId());

        // then
        assertEquals(
            insertedRow,
            found
        );
    }
}