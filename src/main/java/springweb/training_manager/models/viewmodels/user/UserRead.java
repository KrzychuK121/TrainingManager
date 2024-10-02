package springweb.training_manager.models.viewmodels.user;

import springweb.training_manager.models.entities.User;
import springweb.training_manager.models.schemas.UserSchema;

public class UserRead extends UserSchema {

    public UserRead(User user) {
        super(
            user.getId(),
            user.getFirstName(),
            user.getLastName(),
            user.getUsername(),
            ""
        );
    }
}
