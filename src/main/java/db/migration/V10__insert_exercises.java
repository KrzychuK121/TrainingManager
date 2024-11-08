package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import springweb.training_manager.models.entities.Exercise;
import springweb.training_manager.models.entities.ExerciseParameters;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Types;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class V10__insert_exercises extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        String sql = "INSERT INTO PUBLIC.EXERCISE (NAME, DESCRIPTION, ROUNDS, REPETITION, TIME, ID) VALUES (?, ?, ?, ?, ?, ?)";
        var exercises = exercisesToCreate();

        try (PreparedStatement statement = context.getConnection()
            .prepareStatement(sql)) {
            for (Exercise exercise : exercises) {

                statement.setString(1, exercise.getName());
                statement.setString(2, exercise.getDescription());
                statement.setInt(3, exercise.getParameters()
                    .getRounds());
                statement.setInt(4, exercise.getParameters()
                    .getRepetition());
                if (exercise.getParameters()
                    .getTime() == null)
                    statement.setNull(5, Types.TIME);
                else
                    statement.setTime(
                        5,
                        Time.valueOf(
                            exercise.getParameters()
                                .getTime()
                        )
                    );
                statement.setInt(6, exercise.getId());
                statement.addBatch();
            }

            statement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private List<Exercise> exercisesToCreate() {
        List<Exercise> toReturn = new ArrayList<>(4);

        createNewExercise(
            1,
            "Pompki",
            "Zwyczajne pompki, bez utrudnień",
            3,
            20,
            null,
            toReturn
        );

        createNewExercise(
            2,
            "Brzuszki",
            "Brzuszki z obciążeniem 4kg/rękę",
            4,
            20,
            null,
            toReturn
        );

        createNewExercise(
            3,
            "Przeciąganie liny",
            "Ćw na brzuch, udawane przeciąganie liny",
            3,
            0,
            LocalTime.of(0, 0, 45),
            toReturn
        );

        createNewExercise(
            4,
            "Rozpiętki",
            "Przy obciążeniu 5kg/rękę",
            3,
            22,
            LocalTime.of(0, 1, 30),
            toReturn
        );

        return toReturn;
    }

    private void createNewExercise(
        int id,
        String name,
        String description,
        int rounds,
        int repetition,
        LocalTime time,
        List<Exercise> toReturn
    ) {
        Exercise ex = new Exercise(
            name,
            description,
            null,
            new ExerciseParameters(
                0,
                rounds,
                repetition,
                (short) 0,
                time
            ),
            1
        );
        ex.setId(id);

        toReturn.add(ex);
    }

}
