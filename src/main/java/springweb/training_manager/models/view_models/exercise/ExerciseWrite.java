package springweb.training_manager.models.view_models.exercise;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import springweb.training_manager.models.entities.BodyPart;
import springweb.training_manager.models.entities.Exercise;
import springweb.training_manager.models.entities.TrainingExercise;
import springweb.training_manager.models.schemas.ExerciseSchema;
import springweb.training_manager.models.view_models.Castable;
import springweb.training_manager.models.view_models.exercise_parameters.ExerciseParametersWrite;
import springweb.training_manager.models.view_models.training.TrainingExerciseVM;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class ExerciseWrite extends ExerciseSchema implements Castable<Exercise> {
    private List<TrainingExerciseVM> trainings = new ArrayList<>();
    @Valid
    private ExerciseParametersWrite parameters = new ExerciseParametersWrite();
    private boolean exercisePrivate;

    public static List<Exercise> toExerciseList(final List<ExerciseWrite> list) {
        return list.stream()
            .map(
                ExerciseWrite::toEntity
            )
            .collect(Collectors.toList());
    }

    public void setBodyPart(BodyPart bodyPart) {
        this.bodyPart = bodyPart;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDefaultBurnedKcal(int defaultBurnedKcal) {
        this.defaultBurnedKcal = defaultBurnedKcal;
    }

    public void setTime(LocalTime time) {
        this.parameters.setTime(time);
    }

    private List<TrainingExercise> toTrainingExercise(Exercise toMap) {
        return trainings.stream()
            .map(
                customData -> new TrainingExercise(
                    customData.toEntity(),
                    toMap,
                    toMap.getParameters()
                )
            )
            .toList();
    }

    @Override
    public Exercise toEntity() {
        var toReturn = new Exercise(
            name,
            description,
            bodyPart,
            parameters.toEntity(),
            defaultBurnedKcal
        );

        if (trainings != null)
            toReturn.setTrainingExercises(
                toTrainingExercise(toReturn)
            );

        return toReturn;
    }
}