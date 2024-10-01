package springweb.training_manager.models.viewmodels.training;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import springweb.training_manager.models.entities.Training;
import springweb.training_manager.models.schemas.TrainingSchema;
import springweb.training_manager.models.viewmodels.Castable;
import springweb.training_manager.models.viewmodels.exercise.ExerciseTraining;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TrainingWrite extends TrainingSchema implements Castable<Training> {
    private List<ExerciseTraining> exercises = new ArrayList<>();

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public Training toEntity() {
        var toReturn = new Training();

        toReturn.setTitle(title);
        toReturn.setDescription(description);
        if (exercises != null)
            toReturn.setExercises(ExerciseTraining.toExerciseList(exercises));

        return toReturn;
    }
}
