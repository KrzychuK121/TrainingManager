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

public class V17__lowercase_names extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        var usersNewNames = newNames();

        String sql = "UPDATE PUBLIC.IDENTITY_USER SET FIRST_NAME = ?, LAST_NAME = ? WHERE PUBLIC.IDENTITY_USER.ID = ?";
        try (PreparedStatement statement = context.getConnection()
            .prepareStatement(sql)) {
            for (var row : usersNewNames) {
                statement.setString(1, row.getFirstName());
                statement.setString(2, row.getLastName());
                statement.setString(3, row.getUserId());

                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private List<LowercaseNames_V17> newNames() {
        List<LowercaseNames_V17> usersNewNames = new ArrayList<>(2);

        // Admin
        usersNewNames.add(
            new LowercaseNames_V17(
                "078c75cf-ba30-42d0-bfd6-619b89a39093",
                "admin",
                "admin"
            )
        );
        // User
        usersNewNames.add(
            new LowercaseNames_V17(
                "1953e65b-d3a2-48d4-8b34-21e5ae75828a",
                "user",
                "user"
            )
        );

        return usersNewNames;
    }
}

@AllArgsConstructor
@Getter
@Setter
class LowercaseNames_V17 {
    private String userId;
    private String firstName;
    private String lastName;
}