package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import springweb.training_manager.models.entities.TrainingRoutine;
import springweb.training_manager.models.entities.User;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class V33__insert_training_routine extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        String sql = "INSERT INTO TRAINING_ROUTINE (ID, ACTIVE, IDENTITY_USER_ID) VALUES (?, ?, ?)";
        var trainingRoutines = trainingRoutinesToCreate();

        try (PreparedStatement statement = context.getConnection().prepareStatement(sql)) {
            for (var trainingRoutine : trainingRoutines) {
                statement.setInt(1, trainingRoutine.getId());
                statement.setBoolean(2, trainingRoutine.isActive());
                statement.setString(3, trainingRoutine.getOwner().getId());

                statement.addBatch();
            }

            statement.executeBatch();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<TrainingRoutine> trainingRoutinesToCreate(){
        var toReturn  = new ArrayList<TrainingRoutine>();
        User defaultUser = new User();
        defaultUser.setId("1953e65b-d3a2-48d4-8b34-21e5ae75828a");

        toReturn.add(createNewTrainingRoutine(1, true, defaultUser));
        toReturn.add(createNewTrainingRoutine(2, false, defaultUser));

        return toReturn;
    }

    private TrainingRoutine createNewTrainingRoutine(int id, boolean active, User owner){
        var toReturn = new TrainingRoutine();

        toReturn.setId(id);
        toReturn.setActive(active);
        toReturn.setOwner(owner);

        return toReturn;
    }
}
