package springweb.training_manager.models.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;
import springweb.training_manager.models.schemas.ExerciseParametersSchema;

import java.time.LocalTime;

@NoArgsConstructor
@Entity
@Table(name = "exercise_parameters")
public class ExerciseParameters extends ExerciseParametersSchema {
    public ExerciseParameters(
        int id,
        int rounds,
        int repetition,
        short weights,
        LocalTime time
    ) {
        super(
            id,
            rounds,
            repetition,
            weights,
            time
        );
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
}
