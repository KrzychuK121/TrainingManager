package springweb.trainingmanager.models.viewmodels.user;

import springweb.trainingmanager.models.entities.User;
import springweb.trainingmanager.models.schemas.UserSchema;

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
