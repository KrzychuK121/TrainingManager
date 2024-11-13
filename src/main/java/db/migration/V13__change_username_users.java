package db.migration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class V13__change_username_users extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        var usersNewUsernames = newUsernames();

        String sql = "UPDATE PUBLIC.IDENTITY_USER SET USERNAME = ? WHERE PUBLIC.IDENTITY_USER.ID = ?";
        try (PreparedStatement statement = context.getConnection()
            .prepareStatement(sql)) {
            for (var row : usersNewUsernames) {
                statement.setString(1, row.getNewUsername());
                statement.setString(2, row.getUserId());

                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private List<NewUsernames_V13> newUsernames() {
        List<NewUsernames_V13> usersNewUsernames = new ArrayList<>(2);

        // Admin
        usersNewUsernames.add(
            new NewUsernames_V13(
                "078c75cf-ba30-42d0-bfd6-619b89a39093",
                "Administrator"
            )
        );
        // User
        usersNewUsernames.add(
            new NewUsernames_V13(
                "1953e65b-d3a2-48d4-8b34-21e5ae75828a",
                "Uzytkownik"
            )
        );

        return usersNewUsernames;
    }
}

@AllArgsConstructor
@Getter
@Setter
class NewUsernames_V13 {
    private String userId;
    private String newUsername;
}
