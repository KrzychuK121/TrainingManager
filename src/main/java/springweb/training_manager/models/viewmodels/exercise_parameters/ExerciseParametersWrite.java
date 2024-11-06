package springweb.training_manager.models.viewmodels.exercise_parameters;

import lombok.Builder;
import lombok.NoArgsConstructor;
import springweb.training_manager.models.entities.Difficulty;
import springweb.training_manager.models.entities.ExerciseParameters;
import springweb.training_manager.models.schemas.ExerciseParametersSchema;
import springweb.training_manager.models.viewmodels.Castable;

import java.time.LocalTime;

@NoArgsConstructor
public class ExerciseParametersWrite extends ExerciseParametersSchema implements Castable<ExerciseParameters> {
    void setId(int id) {
        this.id = id;
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

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    public ExerciseParameters toEntity() {
        return new ExerciseParameters(
            0,
            rounds,
            repetition,
            weights,
            time,
            difficulty
        );
    }
}
