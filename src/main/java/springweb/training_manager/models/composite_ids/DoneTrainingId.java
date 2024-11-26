package springweb.training_manager.models.composite_ids;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class DoneTrainingId implements Serializable {
    private int routineId;
    private int trainingId;
    private LocalDateTime startDate;
}
