package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class V17__lowercase_names extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        HashMap<String, String[]> usersNewNames = newNames();

        String sql = "UPDATE PUBLIC.IDENTITY_USER SET FIRST_NAME = ?, LAST_NAME = ? WHERE PUBLIC.IDENTITY_USER.ID = ?";
        try (PreparedStatement statement = context.getConnection().prepareStatement(sql)) {
            for (Map.Entry<String, String[]> entry : usersNewNames.entrySet()) {
                String userId = entry.getKey();
                String newFirstname = entry.getValue()[0];
                String newLastname = entry.getValue()[1];

                statement.setString(1, newFirstname);
                statement.setString(2, newLastname);
                statement.setString(3, userId);

                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private HashMap<String, String[]> newNames(){
        HashMap<String, String[]> usersNewNames = new HashMap<>(2);

        // Admin
        usersNewNames.put("078c75cf-ba30-42d0-bfd6-619b89a39093", new String[]{"admin", "admin"});
        // User
        usersNewNames.put("1953e65b-d3a2-48d4-8b34-21e5ae75828a", new String[]{"user", "user"});

        return usersNewNames;
    }
}
