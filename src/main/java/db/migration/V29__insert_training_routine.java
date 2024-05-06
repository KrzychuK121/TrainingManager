package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import springweb.trainingmanager.models.entities.TrainingRoutine;
import springweb.trainingmanager.models.entities.Weekdays;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class V29__insert_training_routine extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        String sql = "INSERT INTO TRAINING_ROUTINE (ID, ACTIVE) VALUES (?, ?)";
        var trainingRoutines = trainingRoutinesToCreate();

        try (PreparedStatement statement = context.getConnection().prepareStatement(sql)) {
            for (var trainingRoutine : trainingRoutines) {
                statement.setInt(1, trainingRoutine.getId());
                statement.setBoolean(2, trainingRoutine.isActive());

                statement.addBatch();
            }

            statement.executeBatch();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<TrainingRoutine> trainingRoutinesToCreate(){
        var toReturn  = new ArrayList<TrainingRoutine>();

        toReturn.add(createNewTrainingRoutine(1, false));
        toReturn.add(createNewTrainingRoutine(2, false));

        return toReturn;
    }

    private TrainingRoutine createNewTrainingRoutine(int id, boolean active){
        var toReturn = new TrainingRoutine();

        toReturn.setId(id);
        toReturn.setActive(active);

        return toReturn;
    }
}
