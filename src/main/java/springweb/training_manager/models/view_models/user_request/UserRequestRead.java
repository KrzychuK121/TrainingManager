package springweb.training_manager.models.view_models.user_request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import springweb.training_manager.models.entities.UserRequest;
import springweb.training_manager.models.view_models.user.RequestUserRead;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class UserRequestRead {
    private int id;
    private String title;
    private String description;
    private LocalDateTime requestDate;
    private RequestUserRead requesting;
    private RequestUserRead closing;

    public UserRequestRead(UserRequest userRequest) {
        this(
            userRequest.getId(),
            userRequest.getTitle(),
            userRequest.getDescription(),
            userRequest.getRequestDate(),
            new RequestUserRead(userRequest.getRequester()),
            userRequest.getClosing() != null
                ? new RequestUserRead(userRequest.getClosing())
                : null
        );
    }
}
