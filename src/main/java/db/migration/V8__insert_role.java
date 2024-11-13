package db.migration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class V8__insert_role extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        var roles = rolesToCreate();

        String sql = "INSERT INTO PUBLIC.ROLES (ID, NAME) VALUES (?, ?)";
        try (PreparedStatement statement = context.getConnection()
            .prepareStatement(sql)) {
            for (var r : roles) {
                statement.setInt(1, r.getId());
                statement.setString(2, r.getName());
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private List<Role_V8> rolesToCreate() {
        List<Role_V8> roles = new ArrayList<>(2);

        // Admin
        var admin = new Role_V8();
        admin.setId(1);
        admin.setName(Role_V8.ROLE_ADMIN);
        roles.add(admin);

        // User
        var user = new Role_V8();
        user.setId(2);
        user.setName(Role_V8.ROLE_USER);
        roles.add(user);

        return roles;
    }
}

@RequiredArgsConstructor
@Getter
@Setter
class Role_V8 {
    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    protected int id;
    protected String name;
}