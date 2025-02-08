package springweb.training_manager.models.view_models.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import springweb.training_manager.models.entities.User;

@AllArgsConstructor
@Getter
public class RequestUserRead {
    private String id;
    private String username;
    private String firstName;
    private String lastName;

    public RequestUserRead(User user) {
        this(
            user.getId(),
            user.getUsername(),
            user.getFirstName(),
            user.getLastName()
        );
    }
}
