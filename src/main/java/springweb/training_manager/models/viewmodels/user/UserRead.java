package springweb.training_manager.models.viewmodels.user;

import springweb.training_manager.models.entities.User;
import springweb.training_manager.models.schemas.UserSchema;

public class UserRead extends UserSchema {

    public UserRead(
        User user
    ) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.username = user.getUsername();
    }
}
