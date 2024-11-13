package db.migration;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class V7__insert_user extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        List<User_V7> users = usersToCreate();

        String sql = "INSERT INTO PUBLIC.IDENTITY_USER (ID, FIRST_NAME, LAST_NAME, PASSWORD, USERNAME, PASSWORD_HASHED) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = context.getConnection()
            .prepareStatement(sql)) {
            for (var u : users) {
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

    private List<User_V7> usersToCreate() {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        List<User_V7> users = new ArrayList<>(2);

        // Admin
        String adminPass = "admin1234";
        var admin = new User_V7();
        admin.setId("078c75cf-ba30-42d0-bfd6-619b89a39093");
        admin.setFirstName("Admin");
        admin.setLastName("Admin");
        admin.setPassword(adminPass);
        admin.setUsername("admin");
        admin.setPasswordHashed(encoder.encode(adminPass));
        users.add(admin);

        // User
        String userPass = "user12345";
        var user = new User_V7();
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

@RequiredArgsConstructor
@Getter
@Setter
class User_V7 {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    protected String id;
    @NotBlank(message = "Imie użytkownika nie może być puste.")
    @Length(max = 25)
    protected String firstName;
    @NotBlank(message = "Nazwisko użytkownika nie może być puste.")
    @Length(max = 30)
    protected String lastName;
    @Column(nullable = false, unique = true)
    @Length(min = 8, max = 20, message = "Nazwa użytkownika musi mieć od 8 do 20 znaków.")
    protected String username;
    @Transient
    @Length(min = 8, max = 30, message = "Hasło musi mieć od 8 do 30 znaków.")
    protected String password;
    private String passwordHashed;
}
