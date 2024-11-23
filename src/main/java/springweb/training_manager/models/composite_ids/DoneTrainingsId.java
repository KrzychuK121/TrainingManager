package springweb.training_manager.models.composite_ids;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class DoneTrainingsId implements Serializable {
    private int routineId;
    private int trainingId;
}
