package db.migration;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import springweb.training_manager.models.entities.BodyPart;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalTime;

@Slf4j
public class V51__insert_cardio_trainings extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        try (PreparedStatement insertTrainingStmt = context.getConnection()
            .prepareStatement(
                "INSERT INTO training (description, title, owner_id) VALUES (?, ?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS
            );
             PreparedStatement insertParamsStmt = context.getConnection()
                 .prepareStatement(
                     "INSERT INTO exercise_parameters (repetition, rounds, time, weights) VALUES (?, ?, ?, ?)",
                     PreparedStatement.RETURN_GENERATED_KEYS
                 );
             PreparedStatement insertExerciseStmt = context.getConnection()
                 .prepareStatement(
                     "INSERT INTO exercise (description, name, body_part, parameters_id, default_burned_kcal, owner_id) VALUES (?, ?, ?, ?, ?, ?)",
                     PreparedStatement.RETURN_GENERATED_KEYS
                 );
             PreparedStatement insertTrainingExerciseStmt = context.getConnection()
                 .prepareStatement(
                     "INSERT INTO training_exercise (training_id, exercise_id, parameters_id) VALUES (?, ?, ?)"
                 )
        ) {

            int training1Id = insertTraining(
                insertTrainingStmt,
                new Training_V51(
                    "Trening polegający tylko na biegu",
                    "Bieganie",
                    null
                )
            );

            int runParamsId = insertExerciseParameters(
                insertParamsStmt,
                new ExerciseParameters_V51(
                    0,
                    1,
                    "00:50:00",
                    0
                )
            );
            int runExerciseId = insertExercise(
                insertExerciseStmt,
                new Exercise_V51(
                    "Spokojny bieg",
                    "Bieg",
                    BodyPart.CARDIO,
                    runParamsId,
                    10,
                    null
                )
            );

            insertTrainingExercise(
                insertTrainingExerciseStmt,
                new TrainingExercise_V51(
                    training1Id,
                    runExerciseId,
                    runParamsId
                )
            );

            int training2Id = insertTraining(
                insertTrainingStmt,
                new Training_V51(
                    "Intensywny trening do wykonania na siłowni",
                    "Trening aerobowy",
                    null
                )
            );

            int cyclingParamsId = insertExerciseParameters(
                insertParamsStmt,
                new ExerciseParameters_V51(
                    0,
                    1,
                    "00:25:00",
                    0
                )
            );
            int cyclingExerciseId = insertExercise(
                insertExerciseStmt,
                new Exercise_V51(
                    "Jazda na rowerze,zwykłym bądź stacjonarnym",
                    "Jazda na rowerze",
                    BodyPart.CARDIO,
                    cyclingParamsId,
                    8,
                    null
                )
            );

            int jumpRopeParamsId = insertExerciseParameters(
                insertParamsStmt,
                new ExerciseParameters_V51(
                    0,
                    2,
                    "00:10:00",
                    0
                )
            );
            int jumpRopeExerciseId = insertExercise(
                insertExerciseStmt,
                new Exercise_V51(
                    "Klasyczne skakanie na skakance normalnym tempem",
                    "Skakanka",
                    BodyPart.CARDIO,
                    jumpRopeParamsId,
                    12,
                    null
                )
            );

            int rowingParamsId = insertExerciseParameters(
                insertParamsStmt,
                new ExerciseParameters_V51(
                    0,
                    3,
                    "00:02:00",
                    5
                )
            );
            int rowingExerciseId = insertExercise(
                insertExerciseStmt,
                new Exercise_V51(
                    "Wiosłowanie z wykorzystaniem maszyny",
                    "Wiosłowanie",
                    BodyPart.CARDIO,
                    rowingParamsId,
                    10,
                    null
                )
            );

            insertTrainingExercise(
                insertTrainingExerciseStmt,
                new TrainingExercise_V51(
                    training2Id,
                    cyclingExerciseId,
                    cyclingParamsId
                )
            );

            insertTrainingExercise(
                insertTrainingExerciseStmt,
                new TrainingExercise_V51(
                    training2Id,
                    jumpRopeExerciseId,
                    jumpRopeParamsId
                )
            );

            insertTrainingExercise(
                insertTrainingExerciseStmt,
                new TrainingExercise_V51(
                    training2Id,
                    rowingExerciseId,
                    rowingParamsId
                )
            );
        }
    }

    private int insertTraining(
        PreparedStatement statement,
        Training_V51 training
    ) throws SQLException {
        statement.setString(1, training.description);
        statement.setString(2, training.title);
        statement.setString(3, training.ownerId);
        statement.executeUpdate();
        return getGeneratedId(statement);
    }

    private int insertExerciseParameters(
        PreparedStatement statement,
        ExerciseParameters_V51 parameters
    ) throws SQLException {
        statement.setInt(1, parameters.repetition);
        statement.setInt(2, parameters.rounds);
        if (parameters.time != null) {
            statement.setTime(
                3,
                Time.valueOf(
                    LocalTime.parse(parameters.time)
                )
            );
        } else {
            statement.setNull(3, java.sql.Types.TIME);
        }
        statement.setInt(4, parameters.weights);
        statement.executeUpdate();
        return getGeneratedId(statement);
    }

    private void insertTrainingExercise(
        PreparedStatement statement,
        TrainingExercise_V51 trainingExercise
    ) throws SQLException {
        statement.setInt(1, trainingExercise.trainingId);
        statement.setInt(2, trainingExercise.exerciseId);
        statement.setInt(3, trainingExercise.parametersId);
        statement.executeUpdate();
    }

    private int insertExercise(
        PreparedStatement statement,
        Exercise_V51 exercise
    ) throws SQLException {
        statement.setString(1, exercise.description);
        statement.setString(2, exercise.name);
        statement.setString(3, exercise.bodyPart.name());
        statement.setInt(4, exercise.parametersId);
        statement.setInt(5, exercise.defaultBurnedKcal);
        statement.setString(6, exercise.ownerId);
        statement.executeUpdate();
        return getGeneratedId(statement);
    }

    private int getGeneratedId(PreparedStatement statement) throws SQLException {
        try (var generatedKeys = statement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            } else {
                throw new SQLException("Failed to retrieve generated key");
            }
        }
    }
}

@AllArgsConstructor
class Exercise_V51 {
    String description;
    String name;
    BodyPart bodyPart;
    int parametersId;
    int defaultBurnedKcal;
    String ownerId;
}

@AllArgsConstructor
class ExerciseParameters_V51 {
    int repetition;
    int rounds;
    String time; // Stored as `TIME` in DB, use `String` for simplicity
    int weights;
}

@AllArgsConstructor
class Training_V51 {
    String description;
    String title;
    String ownerId;
}

@AllArgsConstructor
class TrainingExercise_V51 {
    int trainingId;
    int exerciseId;
    int parametersId;
}
