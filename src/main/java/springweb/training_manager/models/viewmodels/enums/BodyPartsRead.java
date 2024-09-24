package springweb.training_manager.models.viewmodels.enums;

import lombok.Getter;
import springweb.training_manager.models.entities.BodyPart;

import java.util.ArrayList;
import java.util.List;

@Getter
public class BodyPartsRead {
    private final List<BodyPartRead> bodyParts;

    public BodyPartsRead() {
        var elements = BodyPart.values();
        var bodyPartsToSave = new ArrayList<BodyPartRead>(elements.length);
        for (var bodyPart : elements)
            bodyPartsToSave.add(new BodyPartRead(bodyPart));
        bodyParts = bodyPartsToSave;
    }
}
