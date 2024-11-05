package springweb.training_manager.models.viewmodels.exercise;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import springweb.training_manager.models.entities.BodyPart;
import springweb.training_manager.models.entities.Difficulty;
import springweb.training_manager.models.entities.Exercise;
import springweb.training_manager.models.schemas.ExerciseSchema;
import springweb.training_manager.models.viewmodels.Castable;
import springweb.training_manager.models.viewmodels.training.TrainingExerciseVM;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class ExerciseWrite extends ExerciseSchema implements Castable<Exercise> {
    private List<TrainingExerciseVM> trainings = new ArrayList<>();

    public static List<Exercise> toExerciseList(final List<ExerciseWrite> list) {
        return list.stream().map(
            ExerciseWrite::toEntity
        ).collect(Collectors.toList());
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setRounds(int rounds) {
        this.rounds = rounds;
    }

    public void setRepetition(int repetition) {
        this.repetition = repetition;
    }

    public void setWeights(short weights) {
        this.weights = weights;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public void setBodyPart(BodyPart bodyPart) {
        this.bodyPart = bodyPart;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    public Exercise toEntity() {
        var toReturn = new Exercise(
            name,
            description,
            rounds,
            repetition,
            weights,
            time,
            bodyPart,
            difficulty
        );

        if (trainings != null)
            toReturn.setTrainings(TrainingExerciseVM.toTrainingList(trainings));

        return toReturn;
    }
}