package db.migration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class V35__insert_training_plan extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        String sql = "insert into TRAINING_PLAN (ROUTINE_ID, SCHEDULE_ID, TRAINING_TIME) values (?, ?, ?)";
        var trainingPlans = trainingPlansToCreate();
        try (PreparedStatement statement = context.getConnection()
            .prepareStatement(sql)) {
            for (var trainingPlan : trainingPlans) {
                statement.setInt(1, trainingPlan.getRoutineId());
                statement.setInt(2, trainingPlan.getScheduleId());
                statement.setTime(
                    3,
                    Time.valueOf(trainingPlan.getTrainingTime())
                );
                statement.addBatch();
            }

            statement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private List<TrainingPlan_V35> trainingPlansToCreate() {
        var toReturn = new ArrayList<TrainingPlan_V35>();
        var idRoutine1 = 1;
        var idRoutine2 = 2;

        /*var schedulesForRoutine1 = trainingSchedulesToSave(
            createNewTrainingSchedule(1, Weekdays.MONDAY), // id: 1
            createNewTrainingSchedule(1, Weekdays.WEDNESDAY), // id: 2
            createNewTrainingSchedule(2, Weekdays.FRIDAY) // id: 3
        );*/

        /*var schedulesForRoutine2 = trainingSchedulesToSave(
            createNewTrainingSchedule(1, Weekdays.MONDAY), // id: 1
            createNewTrainingSchedule(1, Weekdays.WEDNESDAY), // id: 2
            createNewTrainingSchedule(2, Weekdays.THURSDAY), // id: 4
            createNewTrainingSchedule(3, Weekdays.SATURDAY) // id: 5
        );*/

        // Routine 1
        toReturn.add(
            new TrainingPlan_V35(
                idRoutine1,
                1,
                LocalTime.now()
            )
        );
        toReturn.add(
            new TrainingPlan_V35(
                idRoutine1,
                2,
                LocalTime.now()
            )
        );
        toReturn.add(
            new TrainingPlan_V35(
                idRoutine1,
                3,
                LocalTime.now()
            )
        );

        // Routine 2
        toReturn.add(
            new TrainingPlan_V35(
                idRoutine2,
                1,
                LocalTime.now()
            )
        );
        toReturn.add(
            new TrainingPlan_V35(
                idRoutine2,
                2,
                LocalTime.now()
            )
        );
        toReturn.add(
            new TrainingPlan_V35(
                idRoutine2,
                4,
                LocalTime.now()
            )
        );
        toReturn.add(
            new TrainingPlan_V35(
                idRoutine2,
                5,
                LocalTime.now()
            )
        );

        return toReturn;
    }
}

@AllArgsConstructor
@Getter
@Setter
class TrainingPlan_V35 {
    private int routineId;
    private int scheduleId;
    private LocalTime trainingTime;
}