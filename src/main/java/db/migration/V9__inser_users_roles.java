package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class V9__inser_users_roles extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        String sql = "INSERT INTO PUBLIC.USERS_ROLES (USER_ID, ROLE_ID) VALUES (?, ?)";
        Map<String, Integer> usersRoles = usersRolesToCreate();

        try (PreparedStatement statement = context.getConnection().prepareStatement(sql)) {
            for (Map.Entry<String, Integer> entry : usersRoles.entrySet()) {
                String userId = entry.getKey();
                Integer roleId = entry.getValue();

                statement.setString(1, userId);
                statement.setInt(2, roleId);
                statement.addBatch();
            }

            statement.executeBatch();
        } catch(SQLException e) {
            e.printStackTrace();
        }

    }

    private Map<String, Integer> usersRolesToCreate() {
        Map<String, Integer> usersRoles = new HashMap<>(2);
        // Admin Admin
        usersRoles.put("078c75cf-ba30-42d0-bfd6-619b89a39093", 1);

        // User User
        usersRoles.put("1953e65b-d3a2-48d4-8b34-21e5ae75828a", 2);

        return usersRoles;
    }
}
