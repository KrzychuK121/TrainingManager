package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class V13__change_username_users extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        HashMap<String, String> usersNewUsernames = newUsernames();

        String sql = "UPDATE PUBLIC.IDENTITY_USER SET USERNAME = ? WHERE PUBLIC.IDENTITY_USER.ID = ?";
        try (PreparedStatement statement = context.getConnection().prepareStatement(sql)) {
            for (Map.Entry<String, String> entry : usersNewUsernames.entrySet()) {
                String userId = entry.getKey();
                String newUsername = entry.getValue();

                statement.setString(1, newUsername);
                statement.setString(2, userId);

                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private HashMap<String, String> newUsernames(){
        HashMap<String, String> usersNewUsernames = new HashMap<>(2);

        // Admin
        usersNewUsernames.put("078c75cf-ba30-42d0-bfd6-619b89a39093", "Administrator");
        // User
        usersNewUsernames.put("1953e65b-d3a2-48d4-8b34-21e5ae75828a", "Uzytkownik");

        return usersNewUsernames;
    }
}
