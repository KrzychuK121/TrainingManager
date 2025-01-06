package db.migration;

import lombok.AllArgsConstructor;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class V52__insert_done_trainings_data extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        var doneTrainingRegisterSql = "INSERT INTO public.done_training_register (routine_id, training_id, start_date, end_date, id) VALUES (?, ?, ?, ?, ?)";
        var doneExerciseRegisterSql = "INSERT INTO public.done_exercise_register (id, training_exercise_id, done_series, done_training_register_id) VALUES (?, ?, ?, ?)";
        var doneExercises = getDoneExercises();
        var doneTrainings = getDoneTrainings();

        try (
            PreparedStatement doneTrainingStmt = context.getConnection()
                .prepareStatement(doneTrainingRegisterSql);
            PreparedStatement doneExerciseStmtm = context.getConnection()
                .prepareStatement(doneExerciseRegisterSql);
        ) {
            for (var doneTraining : doneTrainings) {

                doneTrainingStmt.setInt(1, doneTraining.routineId);
                doneTrainingStmt.setInt(2, doneTraining.trainingId);
                if (doneTraining.startDate == null)
                    doneTrainingStmt.setNull(3, Types.DATE);
                else
                    doneTrainingStmt.setTimestamp(
                        3,
                        Timestamp.valueOf(
                            doneTraining.startDate
                        )
                    );
                if (doneTraining.endDate == null)
                    doneTrainingStmt.setNull(4, Types.DATE);
                else
                    doneTrainingStmt.setTimestamp(
                        4,
                        Timestamp.valueOf(
                            doneTraining.endDate
                        )
                    );
                doneTrainingStmt.setInt(5, doneTraining.id);
                doneTrainingStmt.addBatch();
            }

            doneTrainingStmt.executeBatch();

            for (var doneExercise : doneExercises) {

                doneExerciseStmtm.setInt(1, doneExercise.id);
                doneExerciseStmtm.setInt(2, doneExercise.trainingExerciseId);
                doneExerciseStmtm.setInt(3, doneExercise.doneSeries);
                doneExerciseStmtm.setInt(4, doneExercise.doneTrainingRegisterId);

                doneExerciseStmtm.addBatch();
            }

            doneExerciseStmtm.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private List<DoneTraining_V52> getDoneTrainings() {
        var doneTrainings = new ArrayList<DoneTraining_V52>();

        doneTrainings.add(
            new DoneTraining_V52(
                1,
                1,
                "2024-11-27 19:17:22.486000",
                "2024-11-27 19:22:45.538000",
                1
            )
        );

        doneTrainings.add(
            new DoneTraining_V52(
                1,
                2,
                "2024-11-29 19:45:24.000000",
                null,
                2
            )
        );

        doneTrainings.add(
            new DoneTraining_V52(
                2,
                1,
                "2024-12-02 11:40:13.575000",
                "2024-12-02 11:40:18.969000",
                3
            )
        );

        doneTrainings.add(
            new DoneTraining_V52(
                2,
                1,
                "2024-12-04 08:26:38.987000",
                "2024-12-04 08:26:40.805000",
                4
            )
        );

        return doneTrainings;
    }

    private List<DoneExercise_V52> getDoneExercises() {
        var doneExercises = new ArrayList<DoneExercise_V52>();

        doneExercises.add(
            new DoneExercise_V52(
                1,
                1,
                2,
                1
            )
        );

        doneExercises.add(
            new DoneExercise_V52(
                2,
                2,
                3,
                1
            )
        );

        doneExercises.add(
            new DoneExercise_V52(
                3,
                1,
                3,
                3
            )
        );

        doneExercises.add(
            new DoneExercise_V52(
                4,
                2,
                1,
                3
            )
        );

        doneExercises.add(
            new DoneExercise_V52(
                5,
                1,
                3,
                4
            )
        );

        doneExercises.add(
            new DoneExercise_V52(
                6,
                2,
                3,
                4
            )
        );

        return doneExercises;
    }
}

@AllArgsConstructor
class DoneTraining_V52 {
    int routineId;
    int trainingId;
    LocalDateTime startDate;
    LocalDateTime endDate;
    int id;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");

    public DoneTraining_V52(
        int routineId,
        int trainingId,
        String startDate,
        String endDate,
        int id
    ) {
        this(
            routineId,
            trainingId,
            startDate == null
                ? null
                : LocalDateTime.parse(startDate, formatter),
            endDate == null
                ? null
                : LocalDateTime.parse(endDate, formatter),
            id
        );
    }
}

@AllArgsConstructor
class DoneExercise_V52 {
    int id;
    int trainingExerciseId;
    int doneSeries;
    int doneTrainingRegisterId;
}