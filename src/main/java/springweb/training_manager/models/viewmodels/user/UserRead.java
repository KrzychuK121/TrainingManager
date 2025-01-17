package springweb.training_manager.models.viewmodels.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import springweb.training_manager.models.entities.Role;
import springweb.training_manager.models.entities.User;

@AllArgsConstructor
@Getter
public class UserRead {
    private String id;
    private String firstName;
    private String lastName;
    private String username;
    private Role role;
    private boolean locked;

    public UserRead(User user) {
        this(
            user.getId(),
            user.getFirstName(),
            user.getLastName(),
            user.getUsername(),
            user.getRole(),
            user.isLocked()
        );
    }
}
