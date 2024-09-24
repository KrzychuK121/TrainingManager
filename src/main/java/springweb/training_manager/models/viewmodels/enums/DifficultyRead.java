package springweb.training_manager.models.viewmodels.enums;

import lombok.Getter;
import springweb.training_manager.models.entities.Difficulty;

@Getter
public class DifficultyRead {
    private final Difficulty value;
    private final String description;

    public DifficultyRead(Difficulty difficulty) {
        value = difficulty;
        description = Difficulty.getEnumDesc(difficulty);
    }
}
