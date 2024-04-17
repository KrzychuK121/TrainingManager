package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class V19__insert_training_exercise extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        String sql = "INSERT INTO PUBLIC.TRAINING_EXERCISE (TRAINING_ID, EXERCISE_ID) VALUES (?, ?)";
        var trainingExercise = trainingExerciseToCreate();

        try (PreparedStatement statement = context.getConnection().prepareStatement(sql)) {
            for (Map.Entry<Integer, List<Integer>> entry : trainingExercise.entrySet()) {
                Integer trainingId = entry.getKey();
                for(Integer exerciseId : entry.getValue()){
                    statement.setInt(1, trainingId);
                    statement.setInt(2, exerciseId);

                    statement.addBatch();
                }
            }

            statement.executeBatch();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    private HashMap<Integer, List<Integer>> trainingExerciseToCreate(){
        var toReturn  = new HashMap<Integer, List<Integer>>(3);

        toReturn.put(
            1, List.of(1, 4)
        );
        toReturn.put(
            2, List.of(2, 3)
        );
        toReturn.put(
            3, List.of(1, 2, 3, 4)
        );

        return toReturn;
    }
}
