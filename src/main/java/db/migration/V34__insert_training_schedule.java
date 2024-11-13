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

public class V34__insert_training_schedule extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        String sql = "INSERT INTO TRAINING_SCHEDULE (ID, TRAINING_ID, WEEKDAY) values (?, ?, ?)";
        var trainingSchedules = trainingSchedulesToCreate();

        try (PreparedStatement statement = context.getConnection()
            .prepareStatement(sql)) {
            for (var trainingSchedule : trainingSchedules) {
                statement.setInt(1, trainingSchedule.getId());
                statement.setInt(2, trainingSchedule.getTrainingId());
                statement.setString(3, trainingSchedule.getWeekday()
                    .toString());

                statement.addBatch();
            }

            statement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private List<TrainingSchedule_V34> trainingSchedulesToCreate() {
        var toReturn = new ArrayList<TrainingSchedule_V34>();
        // br -> break
        // routine 1
        // 1    2   3   4   5   6   7 <- weekdays
        // t1   br  t1   br  t2  br  br
        // routine 2
        // 1    2   3   4   5   6   7 <- weekdays
        // t1   br  t1  t2  br  t3  br
        toReturn.add(
            new TrainingSchedule_V34(
                1,
                1,
                Weekdays_V34.MONDAY
            )
        ); // routine 1, 2
        toReturn.add(
            new TrainingSchedule_V34(
                2,
                1,
                Weekdays_V34.WEDNESDAY
            )
        ); // routine 1, 2
        toReturn.add(
            new TrainingSchedule_V34(
                3,
                2,
                Weekdays_V34.FRIDAY
            )
        ); // routine 1
        toReturn.add(
            new TrainingSchedule_V34(
                4,
                2,
                Weekdays_V34.THURSDAY
            )
        ); // routine 2
        toReturn.add(
            new TrainingSchedule_V34(
                5,
                3,
                Weekdays_V34.SATURDAY
            )
        ); // routine 2

        return toReturn;
    }

}

@AllArgsConstructor
@Getter
@Setter
class TrainingSchedule_V34 {
    int id;
    int trainingId;
    Weekdays_V34 weekday;
}

enum Weekdays_V34 {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY;
}