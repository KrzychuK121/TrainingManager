package springweb.training_manager.models.viewmodels.enums;

import lombok.Getter;
import springweb.training_manager.models.entities.BodyPart;

@Getter
public class BodyPartRead {
    private final BodyPart value;
    private final String description;

    public BodyPartRead(BodyPart bodyPart) {
        value = bodyPart;
        description = BodyPart.getBodyDesc(bodyPart);
    }
}
