package springweb.training_manager.models.view_models.training;

import lombok.NoArgsConstructor;
import springweb.training_manager.models.entities.Training;
import springweb.training_manager.models.schemas.TrainingSchema;
import springweb.training_manager.models.view_models.Castable;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
public class TrainingExerciseVM extends TrainingSchema implements Castable<Training> {
    public TrainingExerciseVM(Training training) {
        super(
            training.getId(),
            training.getTitle(),
            training.getDescription()
        );
    }

    public static List<TrainingExerciseVM> toTrainingExerciseVMList(final List<Training> list) {
        return list.stream()
            .map(
                TrainingExerciseVM::new
            )
            .collect(Collectors.toList());
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public Training toEntity() {
        var toReturn = new Training(
            title,
            description
        );
        toReturn.setId(id);

        return toReturn;
    }
}
