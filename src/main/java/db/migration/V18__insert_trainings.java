package db.migration;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.hibernate.validator.constraints.Length;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class V18__insert_trainings extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        String sql = "INSERT INTO PUBLIC.TRAINING (ID, TITLE, DESCRIPTION) VALUES (?, ?, ?)";
        var trainings = trainingsToCreate();

        try (PreparedStatement statement = context.getConnection()
            .prepareStatement(sql)) {
            for (var training : trainings) {

                statement.setInt(1, training.getId());
                statement.setString(2, training.getTitle());
                statement.setString(3, training.getDescription());
                statement.addBatch();
            }

            statement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private List<Training_V18> trainingsToCreate() {
        List<Training_V18> toReturn = new ArrayList<>(4);

        toReturn.add(
            new Training_V18(
                1,
                "Klatka piersiowa",
                "Ćwiczenia na rozwój klatki piersiowej"
            )
        );

        toReturn.add(
            new Training_V18(
                2,
                "Trening brzucha",
                "Zbiór ćwiczeń na brzuch"
            )
        );

        toReturn.add(
            new Training_V18(
                3,
                "Klatka + brzuch",
                "Lista ćwiczeń na brzuch i klatkę piersiową"
            )
        );

        return toReturn;
    }
}

@AllArgsConstructor
@Getter
@Setter
class Training_V18 {
    protected int id;
    @NotBlank(message = "Tytuł treningu jest wymagany")
    @Length(min = 3, max = 100, message = "Tytuł musi mieścić się między 3 a 100 znaków")
    protected String title;
    @NotBlank(message = "Opis nie może być pusty")
    @Length(min = 3, max = 300, message = "Opis musi mieścić się między 3 a 300 znaków")
    protected String description;
}
