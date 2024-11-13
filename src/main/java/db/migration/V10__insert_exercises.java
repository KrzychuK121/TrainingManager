package db.migration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

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
            for (var exercise : exercises) {

                statement.setString(1, exercise.getName());
                statement.setString(2, exercise.getDescription());
                statement.setInt(3, exercise.getRounds());
                statement.setInt(4, exercise.getRepetition());
                if (exercise.getTime() == null)
                    statement.setNull(5, Types.TIME);
                else
                    statement.setTime(
                        5,
                        Time.valueOf(
                            exercise.getTime()
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

    private List<Exercise_V10> exercisesToCreate() {
        List<Exercise_V10> toReturn = new ArrayList<>(4);

        toReturn.add(
            new Exercise_V10(
                1,
                "Pompki",
                "Zwyczajne pompki, bez utrudnień",
                3,
                20,
                null
            )
        );

        toReturn.add(
            new Exercise_V10(
                2,
                "Brzuszki",
                "Brzuszki z obciążeniem 4kg/rękę",
                4,
                20,
                null
            )
        );

        toReturn.add(
            new Exercise_V10(
                3,
                "Przeciąganie liny",
                "Ćw na brzuch, udawane przeciąganie liny",
                3,
                0,
                LocalTime.of(0, 0, 45)
            )
        );

        toReturn.add(
            new Exercise_V10(
                4,
                "Rozpiętki",
                "Przy obciążeniu 5kg/rękę",
                3,
                22,
                LocalTime.of(0, 1, 30)
            )
        );

        return toReturn;
    }

}

@AllArgsConstructor
@Getter
@Setter
class Exercise_V10 {
    private int id;
    private String name;
    private String description;
    private int rounds;
    private int repetition;
    private LocalTime time;
}