package springweb.training_manager.models.viewmodels.exercise;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import springweb.training_manager.models.entities.BodyPart;
import springweb.training_manager.models.entities.Difficulty;
import springweb.training_manager.models.entities.Exercise;
import springweb.training_manager.models.schemas.ExerciseSchema;
import springweb.training_manager.models.viewmodels.Castable;
import springweb.training_manager.models.viewmodels.training.TrainingExercise;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class ExerciseWrite extends ExerciseSchema implements Castable<Exercise> {
    private List<TrainingExercise> trainings = new ArrayList<>();

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
        var toReturn = new Exercise();

        toReturn.setName(name);
        toReturn.setDescription(description);
        toReturn.setRounds(rounds);
        toReturn.setRepetition(repetition);
        toReturn.setWeights(weights);
        toReturn.setTime(time);
        toReturn.setBodyPart(bodyPart);
        toReturn.setDifficulty(difficulty);

        if (trainings != null)
            toReturn.setTrainings(TrainingExercise.toTrainingList(trainings));

        return toReturn;
    }
}