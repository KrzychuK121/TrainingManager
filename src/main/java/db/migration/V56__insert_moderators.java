package db.migration;

import lombok.AllArgsConstructor;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import springweb.training_manager.models.entities.Role;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class V56__insert_moderators extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        var users = usersToCreate();

        String sql = "INSERT INTO PUBLIC.IDENTITY_USER (ID, FIRST_NAME, LAST_NAME, USERNAME, PASSWORD_HASHED, ROLE) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = context.getConnection()
            .prepareStatement(sql)) {
            for (var u : users) {
                statement.setString(1, u.id);
                statement.setString(2, u.firstName);
                statement.setString(3, u.lastName);
                statement.setString(4, u.username);
                statement.setString(5, u.passwordHashed);
                statement.setString(6, u.role.toString());
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static List<User_V56> usersToCreate() {
        var users = new ArrayList<User_V56>(2);
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        final var modPassword = "M0d3rator";

        users.add(
            new User_V56(
                "Jan",
                "Kowalski",
                "moderator1",
                encoder.encode(modPassword)
            )
        );

        users.add(
            new User_V56(
                "Joanna",
                "Nowak",
                "moderator2",
                encoder.encode(modPassword)
            )
        );

        return users;
    }
}

@AllArgsConstructor
class User_V56 {
    final String id = UUID.randomUUID()
        .toString();
    String firstName;
    String lastName;
    String username;
    String passwordHashed;

    final Role role = Role.MODERATOR;
}
