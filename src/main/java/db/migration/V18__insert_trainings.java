package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import springweb.trainingmanager.models.entities.Training;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class V18__insert_trainings extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        String sql = "INSERT INTO PUBLIC.TRAINING (ID, TITLE, DESCRIPTION) VALUES (?, ?, ?)";
        var trainings = trainingsToCreate();

        try (PreparedStatement statement = context.getConnection().prepareStatement(sql)) {
            for (Training training : trainings) {

                statement.setInt(1, training.getId());
                statement.setString(2, training.getTitle());
                statement.setString(3, training.getDescription());
                statement.addBatch();
            }

            statement.executeBatch();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    private List<Training> trainingsToCreate(){
        List<Training> toReturn = new ArrayList<>(4);

        createNewTraining(
            1,
            "Klatka piersiowa",
            "Ćwiczenia na rozwój klatki piersiowej",
            toReturn
        );

        createNewTraining(
            2,
            "Trening brzucha",
            "Zbiór ćwiczeń na brzuch",
            toReturn
        );

        createNewTraining(
            3,
            "Klatka + brzuch",
            "Lista ćwiczeń na brzuch i klatkę piersiową",
            toReturn
        );

        return toReturn;
    }

    private void createNewTraining(
        int id,
        String title,
        String description,
        List<Training> toReturn
    ) {
        Training training = new Training();

        training.setId(id);
        training.setTitle(title);
        training.setDescription(description);

        toReturn.add(training);
    }
}
