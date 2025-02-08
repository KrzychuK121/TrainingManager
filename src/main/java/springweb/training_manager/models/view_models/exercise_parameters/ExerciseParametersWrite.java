package springweb.training_manager.models.view_models.exercise_parameters;

import lombok.NoArgsConstructor;
import springweb.training_manager.models.entities.ExerciseParameters;
import springweb.training_manager.models.schemas.ExerciseParametersSchema;
import springweb.training_manager.models.view_models.Castable;

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

    @Override
    public ExerciseParameters toEntity() {
        return new ExerciseParameters(
            0,
            rounds,
            repetition,
            weights,
            time
        );
    }
}
