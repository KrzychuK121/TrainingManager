package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import springweb.training_manager.models.entities.TrainingSchedule;
import springweb.training_manager.models.entities.Weekdays;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class V34__insert_training_schedule extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        String sql = "INSERT INTO TRAINING_SCHEDULE (ID, TRAINING_ID, WEEKDAY) values (?, ?, ?)";
        var trainingSchedules = trainingSchedulesToCreate();

        try (PreparedStatement statement = context.getConnection().prepareStatement(sql)) {
            for (var trainingSchedule : trainingSchedules) {
                statement.setInt(1, trainingSchedule.getId());
                statement.setInt(2, trainingSchedule.getTrainingId());
                statement.setString(3, trainingSchedule.getWeekday().toString());

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
        toReturn.add(createNewTrainingSchedule(1, 1, Weekdays.MONDAY)); // routine 1, 2
        toReturn.add(createNewTrainingSchedule(2, 1, Weekdays.WEDNESDAY)); // routine 1, 2
        toReturn.add(createNewTrainingSchedule(3, 2, Weekdays.FRIDAY)); // routine 1
        toReturn.add(createNewTrainingSchedule(4, 2, Weekdays.THURSDAY)); // routine 2
        toReturn.add(createNewTrainingSchedule(5, 3, Weekdays.SATURDAY)); // routine 2

        return toReturn;
    }

    private TrainingSchedule createNewTrainingSchedule(
        int id,
        int trainingId,
        Weekdays weekday
    ){
        var toReturn = new TrainingSchedule();

        toReturn.setId(id);
        toReturn.setTrainingId(trainingId);
        toReturn.setWeekday(weekday);

        return toReturn;
    }

}
