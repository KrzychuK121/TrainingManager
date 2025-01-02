package db.migration;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import springweb.training_manager.models.entities.BodyPart;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class V51__insert_cardio_trainings extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        var runWorkout = new WholeTrainingData_V51(
            new Training_V51(
                "Trening polegający tylko na biegu",
                "Bieganie",
                null
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Spokojny bieg",
                    "Bieg",
                    BodyPart.CARDIO,
                    10,
                    null
                ),
                new ExerciseParameters_V51(
                    0,
                    1,
                    "00:50:00",
                    0
                )
            )
        );
        var fewTrainingWorkout = new WholeTrainingData_V51(
            new Training_V51(
                "Intensywny trening do wykonania na siłowni",
                "Trening aerobowy",
                null
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Jazda na rowerze, zwykłym bądź stacjonarnym",
                    "Jazda na rowerze",
                    BodyPart.CARDIO,
                    8,
                    null
                ),
                new ExerciseParameters_V51(
                    0,
                    1,
                    "00:25:00",
                    0
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Klasyczne skakanie na skakance normalnym tempem",
                    "Skakanka",
                    BodyPart.CARDIO,
                    12,
                    null
                ),
                new ExerciseParameters_V51(
                    0,
                    2,
                    "00:10:00",
                    0
                )
            ),
            new WholeExerciseData_V51(
                new Exercise_V51(
                    "Wiosłowanie z wykorzystaniem maszyny",
                    "Wiosłowanie",
                    BodyPart.CARDIO,
                    10,
                    null
                ),
                new ExerciseParameters_V51(
                    0,
                    3,
                    "00:02:00",
                    5
                )
            )
        );

        insertWholeTrainingData(
            context,
            runWorkout,
            fewTrainingWorkout
        );
    }

    static void insertWholeTrainingData(
        Context context,
        WholeTrainingData_V51... wholeTrainingsData
    ) throws SQLException {
        final var INSERT_TRAINING_SQL = "INSERT INTO training (description, title, owner_id) VALUES (?, ?, ?)";
        final var SELECT_PARAMS_SQL = (
            "SELECT ep.id FROM exercise_parameters ep " +
                "WHERE ep.rounds = ? " +
                "AND ep.repetition =  ? " +
                "AND NULLIF(ep.time, ?) IS NULL " +
                "AND ep.weights = ?"
        );
        final var INSERT_PARAMS_SQL = "INSERT INTO exercise_parameters (repetition, rounds, time, weights) VALUES (?, ?, ?, ?)";
        final var SELECT_EXERCISE_SQL = (
            "SELECT e.id FROM exercise e " +
                "WHERE e.name = ? " +
                "AND e.description =  ? " +
                "AND e.default_burned_kcal = ?"
        );
        final var INSERT_EXERCISE_SQL = "INSERT INTO exercise (description, name, body_part, parameters_id, default_burned_kcal, owner_id) VALUES (?, ?, ?, ?, ?, ?)";
        final var INSERT_TRAINING_EXERCISE_SQL = "INSERT INTO training_exercise (training_id, exercise_id, parameters_id) VALUES (?, ?, ?)";

        try (
            PreparedStatement insertTrainingStmt = context.getConnection()
                .prepareStatement(INSERT_TRAINING_SQL, PreparedStatement.RETURN_GENERATED_KEYS);
            PreparedStatement selectParamsStmt = context.getConnection()
                .prepareStatement(SELECT_PARAMS_SQL);
            PreparedStatement insertParamsStmt = context.getConnection()
                .prepareStatement(INSERT_PARAMS_SQL, PreparedStatement.RETURN_GENERATED_KEYS);
            PreparedStatement selectExerciseStmt = context.getConnection()
                .prepareStatement(SELECT_EXERCISE_SQL);
            PreparedStatement insertExerciseStmt = context.getConnection()
                .prepareStatement(INSERT_EXERCISE_SQL, PreparedStatement.RETURN_GENERATED_KEYS);
            PreparedStatement insertTrainingExerciseStmt = context.getConnection()
                .prepareStatement(INSERT_TRAINING_EXERCISE_SQL)
        ) {
            Arrays.stream(wholeTrainingsData)
                .forEach(
                    wholeTrainingData -> {
                        try {
                            insertWholeTrainingData(
                                wholeTrainingData,
                                insertTrainingStmt,
                                selectParamsStmt,
                                insertParamsStmt,
                                selectExerciseStmt,
                                insertExerciseStmt,
                                insertTrainingExerciseStmt
                            );
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                );
        }
    }

    private static void insertWholeTrainingData(
        WholeTrainingData_V51 wholeTrainingData,
        PreparedStatement insertTrainingStmt,
        PreparedStatement selectParamsStmt,
        PreparedStatement insertParamsStmt,
        PreparedStatement selectExerciseStmt,
        PreparedStatement insertExerciseStmt,
        PreparedStatement insertTrainingExerciseStmt
    ) throws SQLException {
        var wholeExercisesData = wholeTrainingData.exercises;
        var newTrainingId = insertTraining(
            insertTrainingStmt,
            wholeTrainingData.training
        );

        wholeExercisesData.forEach(
            exerciseData -> {
                try {
                    var parametersId = selectExerciseParameters(
                        selectParamsStmt,
                        exerciseData.parameters
                    );

                    if (parametersId == 0)
                        parametersId = insertExerciseParameters(
                            insertParamsStmt,
                            exerciseData.parameters
                        );

                    var exerciseToSave = exerciseData.exercise;
                    var exerciseId = selectExercise(
                        selectExerciseStmt,
                        exerciseToSave
                    );

                    if (exerciseId == 0) {
                        exerciseToSave.parametersId = parametersId;
                        exerciseId = insertExercise(
                            insertExerciseStmt,
                            exerciseToSave
                        );
                    }

                    insertTrainingExercise(
                        insertTrainingExerciseStmt,
                        new TrainingExercise_V51(
                            newTrainingId,
                            exerciseId,
                            parametersId
                        )
                    );
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        );
    }

    private static int insertTraining(
        PreparedStatement statement,
        Training_V51 training
    ) throws SQLException {
        statement.setString(1, training.description);
        statement.setString(2, training.title);
        statement.setString(3, training.ownerId);
        statement.executeUpdate();
        return getGeneratedId(statement);
    }

    private static int selectExerciseParameters(
        PreparedStatement statement,
        ExerciseParameters_V51 parameters
    ) throws SQLException {
        statement.setInt(1, parameters.rounds);
        statement.setInt(2, parameters.repetition);
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

        ResultSet response = statement.executeQuery();

        return response.next()
            ? response.getInt("id")
            : 0;
    }

    private static int insertExerciseParameters(
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

    private static void insertTrainingExercise(
        PreparedStatement statement,
        TrainingExercise_V51 trainingExercise
    ) throws SQLException {
        statement.setInt(1, trainingExercise.trainingId);
        statement.setInt(2, trainingExercise.exerciseId);
        statement.setInt(3, trainingExercise.parametersId);
        statement.executeUpdate();
    }

    private static int selectExercise(
        PreparedStatement statement,
        Exercise_V51 exercise
    ) throws SQLException {
        statement.setString(1, exercise.name);
        statement.setString(2, exercise.description);
        statement.setInt(3, exercise.defaultBurnedKcal);

        ResultSet response = statement.executeQuery();

        return response.next()
            ? response.getInt("id")
            : 0;
    }

    private static int insertExercise(
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

    private static int getGeneratedId(PreparedStatement statement) throws SQLException {
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
class Training_V51 {
    String description;
    String title;
    String ownerId;
}

@AllArgsConstructor
class Exercise_V51 {
    String description;
    String name;
    BodyPart bodyPart;
    int parametersId;
    int defaultBurnedKcal;
    String ownerId;

    public Exercise_V51(
        String description,
        String name,
        BodyPart bodyPart,
        int defaultBurnedKcal,
        String ownerId
    ) {
        this(
            description,
            name,
            bodyPart,
            0,
            defaultBurnedKcal,
            ownerId
        );
    }
}

@AllArgsConstructor
class ExerciseParameters_V51 {
    int repetition;
    int rounds;
    String time;
    int weights;
}

@AllArgsConstructor
class TrainingExercise_V51 {
    int trainingId;
    int exerciseId;
    int parametersId;
}

@AllArgsConstructor
class WholeExerciseData_V51 {
    Exercise_V51 exercise;
    ExerciseParameters_V51 parameters;
}

@AllArgsConstructor
class WholeTrainingData_V51 {
    Training_V51 training;
    List<WholeExerciseData_V51> exercises;

    public WholeTrainingData_V51(
        Training_V51 training,
        WholeExerciseData_V51... exercises
    ) {
        this.training = training;
        this.exercises = Arrays.asList(exercises);
    }

}