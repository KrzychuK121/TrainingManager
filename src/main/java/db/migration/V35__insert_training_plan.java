package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import springweb.trainingmanager.models.entities.TrainingPlan;
import springweb.trainingmanager.models.schemas.TrainingPlanId;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;

public class V35__insert_training_plan extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        String sql = "insert into TRAINING_PLAN (ROUTINE_ID, SCHEDULE_ID, TRAINING_TIME) values (?, ?, ?)";
        var trainingPlans = trainingPlansToCreate();
        try (PreparedStatement statement = context.getConnection().prepareStatement(sql)) {
            for (var trainingPlan : trainingPlans) {
                statement.setInt(1, trainingPlan.getTrainingRoutineId());
                statement.setInt(2, trainingPlan.getTrainingScheduleId());
                statement.setString(3, trainingPlan.getTrainingTime().toString());
                statement.addBatch();
            }

            statement.executeBatch();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<TrainingPlan> trainingPlansToCreate(){
        var toReturn  = new ArrayList<TrainingPlan>();
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
            createNewTrainingPlan(
                new TrainingPlanId(idRoutine1, 1),
                LocalTime.now()
            )
        );
        toReturn.add(
            createNewTrainingPlan(
                new TrainingPlanId(idRoutine1, 2),
                LocalTime.now()
            )
        );
        toReturn.add(
            createNewTrainingPlan(
                new TrainingPlanId(idRoutine1, 3),
                LocalTime.now()
            )
        );

        // Routine 2
        toReturn.add(
            createNewTrainingPlan(
                new TrainingPlanId(idRoutine2, 1),
                LocalTime.now()
            )
        );
        toReturn.add(
            createNewTrainingPlan(
                new TrainingPlanId(idRoutine2, 2),
                LocalTime.now()
            )
        );
        toReturn.add(
            createNewTrainingPlan(
                new TrainingPlanId(idRoutine2, 4),
                LocalTime.now()
            )
        );
        toReturn.add(
            createNewTrainingPlan(
                new TrainingPlanId(idRoutine2, 5),
                LocalTime.now()
            )
        );

        return toReturn;
    }

    private TrainingPlan createNewTrainingPlan(
        TrainingPlanId id,
        LocalTime trainingTime
    ){
        var toReturn = new TrainingPlan();

        toReturn.setId(id);
        toReturn.setTrainingTime(trainingTime);

        return toReturn;
    }
}
