package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import springweb.training_manager.models.entities.Role;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class V8__insert_role extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        List<Role> roles = rolesToCreate();

        String sql = "INSERT INTO PUBLIC.ROLES (ID, NAME) VALUES (?, ?)";
        try (PreparedStatement statement = context.getConnection().prepareStatement(sql)) {
            for(Role r : roles){
                statement.setInt(1, r.getId());
                statement.setString(2, r.getName());
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private List<Role> rolesToCreate(){
        List<Role> roles = new ArrayList<>(2);

        // Admin
        Role admin = new Role();
        admin.setId(1);
        admin.setName("ROLE_ADMIN");
        roles.add(admin);

        // User
        Role user = new Role();
        user.setId(2);
        user.setName("ROLE_USER");
        roles.add(user);

        return roles;
    }
}
