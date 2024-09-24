package springweb.training_manager.models.viewmodels.enums;

import lombok.Getter;
import springweb.training_manager.models.entities.Difficulty;

import java.util.ArrayList;
import java.util.List;

@Getter
public class DifficultiesRead {
    private final List<DifficultyRead> difficulties;

    public DifficultiesRead() {
        var elements = Difficulty.values();
        var difficultiesToSave = new ArrayList<DifficultyRead>(elements.length);
        for (var difficulty : elements)
            difficultiesToSave.add(new DifficultyRead(difficulty));
        difficulties = difficultiesToSave;
    }
}
