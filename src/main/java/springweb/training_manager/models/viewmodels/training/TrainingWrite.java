package springweb.training_manager.models.viewmodels.training;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import springweb.training_manager.models.entities.Training;
import springweb.training_manager.models.entities.TrainingExercise;
import springweb.training_manager.models.schemas.TrainingSchema;
import springweb.training_manager.models.viewmodels.Castable;
import springweb.training_manager.models.viewmodels.training_exercise.CustomTrainingParametersWrite;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TrainingWrite extends TrainingSchema implements Castable<Training> {
    @Valid
    private List<CustomTrainingParametersWrite> exercises = new ArrayList<>();
    private boolean trainingPrivate;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private List<TrainingExercise> toTrainingExercise(Training toMap) {
        return exercises.stream()
            .map(
                customData -> new TrainingExercise(
                    toMap,
                    customData.getExerciseWrite()
                        .toEntity(),
                    customData.getParameters()
                )
            )
            .toList();
    }

    @Override
    public Training toEntity() {
        var toReturn = new Training(
            title,
            description
        );

        if (exercises != null)
            toReturn.setTrainingExercises(
                toTrainingExercise(toReturn)
            );

        return toReturn;
    }
}
