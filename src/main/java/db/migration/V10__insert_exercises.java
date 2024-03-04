package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import springweb.trainingmanager.models.entities.Exercise;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class V10__insert_exercises extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        String sql = "INSERT INTO PUBLIC.EXERCISE (NAME, DESCRIPTION, ROUNDS, REPETITION, TIME) VALUES (?, ?, ?, ?, ?)";
        var exercises = exercisesToCreate();

        try (PreparedStatement statement = context.getConnection().prepareStatement(sql)) {
            for (Exercise exercise : exercises) {

                statement.setString(1, exercise.getName());
                statement.setString(2, exercise.getDescription());
                statement.setInt(3, exercise.getRounds());
                statement.setInt(4, exercise.getRepetition());
                statement.setString(5, exercise.getTime() == null ? null : exercise.getTime().toString());
                statement.addBatch();
            }

            statement.executeBatch();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    private List<Exercise> exercisesToCreate(){
        List<Exercise> toReturn = new ArrayList<>(4);

        createNewExercise(
            "Pompki",
            "Zwyczajne pompki, bez utrudnień",
            3,
            20,
            null,
            toReturn
        );

        createNewExercise(
            "Brzuszki",
            "Brzuszki z obciążeniem 4kg/rękę",
            4,
            20,
            null,
            toReturn
        );

        createNewExercise(
            "Przeciąganie liny",
            "Ćw na brzuch, udawane przeciąganie liny",
            3,
            0,
            LocalTime.of(0, 0, 45),
            toReturn
        );

        createNewExercise(
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
        String name,
        String description,
        int rounds,
        int repetition,
        LocalTime time,
        List<Exercise> toReturn
    ) {
        Exercise ex = new Exercise();

        ex.setName(name);
        ex.setDescription(description);
        ex.setRounds(rounds);
        ex.setRepetition(repetition);
        ex.setTime(time);

        toReturn.add(ex);
    }

}
