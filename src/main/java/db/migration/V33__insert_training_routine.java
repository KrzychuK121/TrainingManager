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

public class V33__insert_training_routine extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        String sql = "INSERT INTO TRAINING_ROUTINE (ID, ACTIVE, IDENTITY_USER_ID) VALUES (?, ?, ?)";
        var trainingRoutines = trainingRoutinesToCreate();

        try (PreparedStatement statement = context.getConnection()
            .prepareStatement(sql)) {
            for (var trainingRoutine : trainingRoutines) {
                statement.setInt(1, trainingRoutine.getId());
                statement.setBoolean(2, trainingRoutine.isActive());
                statement.setString(3, trainingRoutine.getUserId());

                statement.addBatch();
            }

            statement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private List<TrainingRoutine_V33> trainingRoutinesToCreate() {
        var toReturn = new ArrayList<TrainingRoutine_V33>(2);
        var defaultUserId = "1953e65b-d3a2-48d4-8b34-21e5ae75828a";

        toReturn.add(
            new TrainingRoutine_V33(
                1,
                true,
                defaultUserId
            )
        );
        toReturn.add(
            new TrainingRoutine_V33(
                2,
                false,
                defaultUserId
            )
        );

        return toReturn;
    }
}

@AllArgsConstructor
@Getter
@Setter
class TrainingRoutine_V33 {
    private int id;
    private boolean active;
    private String userId;
}
