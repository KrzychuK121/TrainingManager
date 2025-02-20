package springweb.training_manager.models.view_models.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import springweb.training_manager.models.entities.User;

@AllArgsConstructor
@Getter
public class ScheduleOwner {
    private final String id;
    private final String username;

    public ScheduleOwner(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
    }
}
