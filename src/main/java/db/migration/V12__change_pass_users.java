package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import springweb.trainingmanager.models.entities.User;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class V12__change_pass_users extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        HashMap<String, String> usersNewPasswords = newPasswords();

        String sql = "UPDATE PUBLIC.IDENTITY_USER SET PASSWORD_HASHED = ? WHERE PUBLIC.IDENTITY_USER.ID = ?";
        try (PreparedStatement statement = context.getConnection().prepareStatement(sql)) {
            for (Map.Entry<String, String> entry : usersNewPasswords.entrySet()) {
                String userId = entry.getKey();
                String newPassword = entry.getValue();

                statement.setString(1, newPassword);
                statement.setString(2, userId);

                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private HashMap<String, String> newPasswords(){
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        HashMap<String, String> usersNewPasswords = new HashMap<>(2);

        // Admin
        usersNewPasswords.put("078c75cf-ba30-42d0-bfd6-619b89a39093", encoder.encode("Adm1nP@ss"));
        // User
        usersNewPasswords.put("1953e65b-d3a2-48d4-8b34-21e5ae75828a", encoder.encode("UserP@ssw0rd"));

        return usersNewPasswords;
    }
}
