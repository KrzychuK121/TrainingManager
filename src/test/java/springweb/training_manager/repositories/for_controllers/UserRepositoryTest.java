package springweb.training_manager.repositories.for_controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import springweb.training_manager.models.entities.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
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