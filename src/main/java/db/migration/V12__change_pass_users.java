package db.migration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class V12__change_pass_users extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        var usersNewPasswords = newPasswords();

        String sql = "UPDATE PUBLIC.IDENTITY_USER SET PASSWORD_HASHED = ? WHERE PUBLIC.IDENTITY_USER.ID = ?";
        try (PreparedStatement statement = context.getConnection()
            .prepareStatement(sql)) {
            for (var row : usersNewPasswords) {
                statement.setString(1, row.getNewPassword());
                statement.setString(2, row.getUserId());

                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private List<PasswordChange_V12> newPasswords() {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        List<PasswordChange_V12> usersNewPasswords = new ArrayList<>(2);

        // Admin
        usersNewPasswords.add(
            new PasswordChange_V12(
                "078c75cf-ba30-42d0-bfd6-619b89a39093",
                encoder.encode("Adm1nP@ss")
            )
        );
        // User
        usersNewPasswords.add(
            new PasswordChange_V12(
                "1953e65b-d3a2-48d4-8b34-21e5ae75828a",
                encoder.encode("UserP@ssw0rd")
            )
        );

        return usersNewPasswords;
    }

}

@AllArgsConstructor
@Getter
@Setter
class PasswordChange_V12 {
    private String userId;
    private String newPassword;
}

