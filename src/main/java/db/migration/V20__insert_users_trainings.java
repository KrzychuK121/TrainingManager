package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class V20__insert_users_trainings extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        String sql = "INSERT INTO PUBLIC.USERS_TRAININGS (USER_ID, TRAINING_ID) VALUES (?, ?)";
        var usersTrainings = usersTrainingsToCreate();

        try (PreparedStatement statement = context.getConnection().prepareStatement(sql)) {
            for (Map.Entry<String, List<Integer>> entry : usersTrainings.entrySet()) {
                String userId = entry.getKey();
                for(Integer trainingId : entry.getValue()){
                    statement.setString(1, userId);
                    statement.setInt(2, trainingId);

                    statement.addBatch();
                }
            }

            statement.executeBatch();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    private HashMap<String, List<Integer>> usersTrainingsToCreate(){
        var toReturn  = new HashMap<String, List<Integer>>(1);

        toReturn.put(
            "1953e65b-d3a2-48d4-8b34-21e5ae75828a", List.of(3)
        );

        return toReturn;
    }
}
