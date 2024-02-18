package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import springweb.trainingmanager.models.entities.User;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class V7__insert_user extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        List<User> users = usersToCreate();

        String sql = "INSERT INTO PUBLIC.IDENTITY_USER (ID, FIRST_NAME, LAST_NAME, PASSWORD, USERNAME, PASSWORD_HASHED) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = context.getConnection().prepareStatement(sql)) {
            for(User u : users){
                statement.setString(1, u.getId());
                statement.setString(2, u.getFirstName());
                statement.setString(3, u.getLastName());
                statement.setString(4, u.getPassword());
                statement.setString(5, u.getUsername());
                statement.setString(6, u.getPasswordHashed());
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private List<User> usersToCreate(){
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        List<User> users = new ArrayList<>(2);

        // Admin
        String adminPass = "admin1234";
        User admin = new User();
        admin.setId("078c75cf-ba30-42d0-bfd6-619b89a39093");
        admin.setFirstName("Admin");
        admin.setLastName("Admin");
        admin.setPassword(adminPass);
        admin.setUsername("admin");
        admin.setPasswordHashed(encoder.encode(adminPass));
        users.add(admin);

        // User
        String userPass = "user12345";
        User user = new User();
        user.setId("1953e65b-d3a2-48d4-8b34-21e5ae75828a");
        user.setFirstName("User");
        user.setLastName("User");
        user.setPassword(userPass);
        user.setUsername("user");
        user.setPasswordHashed(encoder.encode(userPass));
        users.add(user);

        return users;
    }
}
