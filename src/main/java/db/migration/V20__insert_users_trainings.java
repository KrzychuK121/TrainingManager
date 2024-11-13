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

public class V20__insert_users_trainings extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        String sql = "INSERT INTO PUBLIC.USERS_TRAININGS (USER_ID, TRAINING_ID) VALUES (?, ?)";
        var usersTrainings = usersTrainingsToCreate();

        try (PreparedStatement statement = context.getConnection()
            .prepareStatement(sql)) {
            for (var row : usersTrainings) {
                statement.setString(1, row.getUserId());
                statement.setInt(2, row.getTrainingId());

                statement.addBatch();
            }

            statement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private List<UserTraining_V20> usersTrainingsToCreate() {
        List<UserTraining_V20> toReturn = new ArrayList<>(1);

        toReturn.addAll(
            UserTraining_V20.getByTrainingIds(
                "1953e65b-d3a2-48d4-8b34-21e5ae75828a",
                List.of(3)
            )
        );

        return toReturn;
    }
}

@AllArgsConstructor
@Getter
@Setter
class UserTraining_V20 {
    private String userId;
    private int trainingId;

    static List<UserTraining_V20> getByTrainingIds(
        String userId,
        List<Integer> trainingIds
    ) {
        return trainingIds.stream()
            .map(
                trainingId -> new UserTraining_V20(
                    userId,
                    trainingId
                )
            )
            .toList();
    }
}
