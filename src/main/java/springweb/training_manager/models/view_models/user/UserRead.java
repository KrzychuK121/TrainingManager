package springweb.training_manager.models.view_models.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import springweb.training_manager.models.entities.User;

@AllArgsConstructor
@Getter
public class UserRead {
    private String id;
    private String firstName;
    private String lastName;
    private String username;
    private RoleRead role;
    private boolean locked;

    public UserRead(User user) {
        this(
            user.getId(),
            user.getFirstName(),
            user.getLastName(),
            user.getUsername(),
            new RoleRead(user.getRole()),
            user.isLocked()
        );
    }
}
