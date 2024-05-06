package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import springweb.trainingmanager.models.entities.TrainingSchedule;
import springweb.trainingmanager.models.entities.Weekdays;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class V28__insert_training_schedule extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        String sql = "INSERT INTO TRAINING_SCHEDULE (TRAINING_ID, WEEKDAY) values (?, ?)";
        var trainingSchedules = trainingSchedulesToCreate();

        try (PreparedStatement statement = context.getConnection().prepareStatement(sql)) {
            for (var trainingSchedule : trainingSchedules) {
                statement.setInt(1, trainingSchedule.getTrainingId());
                statement.setString(2, trainingSchedule.getWeekday().toString());

                statement.addBatch();
            }

            statement.executeBatch();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<TrainingSchedule> trainingSchedulesToCreate(){
        var toReturn  = new ArrayList<TrainingSchedule>();
        // br -> break
        // routine 1
        // 1    2   3   4   5   6   7 <- weekdays
        // t1   br  t1   br  t2  br  br
        // routine 2
        // 1    2   3   4   5   6   7 <- weekdays
        // t1   br  t1  t2  br  t3  br
        toReturn.add(createNewTrainingSchedule(1, Weekdays.MONDAY)); // routine 1, 2
        toReturn.add(createNewTrainingSchedule(1, Weekdays.WEDNESDAY)); // routine 1, 2
        toReturn.add(createNewTrainingSchedule(2, Weekdays.FRIDAY)); // routine 1
        toReturn.add(createNewTrainingSchedule(2, Weekdays.THURSDAY)); // routine 2
        toReturn.add(createNewTrainingSchedule(3, Weekdays.SATURDAY)); // routine 2

        return toReturn;
    }

    private TrainingSchedule createNewTrainingSchedule(int trainingId, Weekdays weekday){
        var toReturn = new TrainingSchedule();

        toReturn.setTrainingId(trainingId);
        toReturn.setWeekday(weekday);

        return toReturn;
    }

}
