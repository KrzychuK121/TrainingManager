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

public class V9__insert_users_roles extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        String sql = "INSERT INTO PUBLIC.USERS_ROLES (USER_ID, ROLE_ID) VALUES (?, ?)";
        var usersRoles = usersRolesToCreate();

        try (PreparedStatement statement = context.getConnection()
            .prepareStatement(sql)) {
            for (var row : usersRoles) {
                statement.setString(1, row.getUserId());
                statement.setInt(2, row.getRoleId());
                statement.addBatch();
            }

            statement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private List<UsersRoles_V9> usersRolesToCreate() {
        List<UsersRoles_V9> usersRoles = new ArrayList<>(2);
        // Admin Admin
        usersRoles.add(
            new UsersRoles_V9("078c75cf-ba30-42d0-bfd6-619b89a39093", 1)
        );

        // User User
        usersRoles.add(
            new UsersRoles_V9("1953e65b-d3a2-48d4-8b34-21e5ae75828a", 2)
        );

        return usersRoles;
    }
}

@AllArgsConstructor
@Getter
@Setter
class UsersRoles_V9 {
    private String userId;
    private Integer roleId;
}