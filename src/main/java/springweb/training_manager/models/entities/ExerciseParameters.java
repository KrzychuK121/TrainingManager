package springweb.training_manager.models.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.NoArgsConstructor;
import springweb.training_manager.models.schemas.ExerciseParametersSchema;

import java.time.LocalTime;

@NoArgsConstructor
@Entity
public class ExerciseParameters extends ExerciseParametersSchema {
    public ExerciseParameters(
        int id,
        int rounds,
        int repetition,
        short weights,
        LocalTime time,
        Difficulty difficulty
    ){
        super(
            id,
            rounds,
            repetition,
            weights,
            time,
            difficulty
        );
    }
}
