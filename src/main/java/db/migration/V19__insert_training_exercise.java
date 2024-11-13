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

public class V19__insert_training_exercise extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        String sql = "INSERT INTO PUBLIC.TRAINING_EXERCISE (TRAINING_ID, EXERCISE_ID) VALUES (?, ?)";
        var trainingExercise = trainingExerciseToCreate();

        try (PreparedStatement statement = context.getConnection()
            .prepareStatement(sql)) {
            for (var row : trainingExercise) {
                statement.setInt(1, row.getTrainingId());
                statement.setInt(2, row.getExerciseId());

                statement.addBatch();
            }

            statement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private List<TrainingExercise_V19> trainingExerciseToCreate() {
        var toReturn = new ArrayList<TrainingExercise_V19>(3);

        toReturn.addAll(
            TrainingExercise_V19.getByExerciseIds(
                1,
                List.of(1, 4)
            )
        );
        toReturn.addAll(
            TrainingExercise_V19.getByExerciseIds(
                2,
                List.of(2, 3)
            )
        );
        toReturn.addAll(
            TrainingExercise_V19.getByExerciseIds(
                3,
                List.of(1, 2, 3, 4)
            )
        );

        return toReturn;
    }
}

@AllArgsConstructor
@Getter
@Setter
class TrainingExercise_V19 {
    private int trainingId;
    private int exerciseId;

    static List<TrainingExercise_V19> getByExerciseIds(
        int trainingId,
        List<Integer> exerciseIds
    ) {
        return exerciseIds.stream()
            .map(
                exerciseId -> new TrainingExercise_V19(
                    trainingId,
                    exerciseId
                )
            )
            .toList();
    }
}