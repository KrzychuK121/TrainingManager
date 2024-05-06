package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import springweb.trainingmanager.models.entities.Training;
import springweb.trainingmanager.models.entities.TrainingRoutine;
import springweb.trainingmanager.models.entities.TrainingSchedule;
import springweb.trainingmanager.models.entities.Weekdays;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class V30__insert_training_routine_schedule extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        String sql = "insert into TRAINING_ROUTINE_SCHEDULE (ROUTINE_ID, TRAINING_ID, WEEKDAY) values (?, ?, ?)";
        var trainingRoutines = trainingRoutinesToCreate();
        try (PreparedStatement statement = context.getConnection().prepareStatement(sql)) {
            for (var trainingRoutine : trainingRoutines) {
                int id = trainingRoutine.getId();
                for(var trainingSchedule : trainingRoutine.getSchedules()){
                    statement.setInt(1, id);
                    statement.setInt(2, trainingSchedule.getTrainingId());
                    statement.setString(3, trainingSchedule.getWeekday().toString());
                    statement.addBatch();
                }
            }

            statement.executeBatch();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    private HashSet<TrainingSchedule> trainingSchedulesToSave(TrainingSchedule ... toAdd){
        var toReturn  = new HashSet<TrainingSchedule>();
        Collections.addAll(toReturn, toAdd);
        return toReturn;
    }

    private TrainingSchedule createNewTrainingSchedule(int trainingId, Weekdays weekday){
        var toReturn = new TrainingSchedule();

        toReturn.setTrainingId(trainingId);
        toReturn.setWeekday(weekday);

        return toReturn;
    }

    private ArrayList<TrainingRoutine> trainingRoutinesToCreate(){
        var toReturn  = new ArrayList<TrainingRoutine>();

        var schedulesForRoutine1 = trainingSchedulesToSave(
            createNewTrainingSchedule(1, Weekdays.MONDAY),
            createNewTrainingSchedule(1, Weekdays.WEDNESDAY),
            createNewTrainingSchedule(2, Weekdays.FRIDAY)
        );

        var schedulesForRoutine2 = trainingSchedulesToSave(
            createNewTrainingSchedule(1, Weekdays.MONDAY),
            createNewTrainingSchedule(1, Weekdays.WEDNESDAY),
            createNewTrainingSchedule(2, Weekdays.THURSDAY),
            createNewTrainingSchedule(3, Weekdays.SATURDAY)
        );

        toReturn.add(createNewTrainingRoutine(1, schedulesForRoutine1));
        toReturn.add(createNewTrainingRoutine(2, schedulesForRoutine2));

        return toReturn;
    }

    private TrainingRoutine createNewTrainingRoutine(int id, Set<TrainingSchedule> trainingSchedules){
        var toReturn = new TrainingRoutine();

        toReturn.setId(id);
        toReturn.setSchedules(trainingSchedules);

        return toReturn;
    }
}
