package springweb.training_manager.models.viewmodels.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import springweb.training_manager.models.entities.Role;

@AllArgsConstructor
@Getter
public class RoleRead {
    private Role role;
    private String roleDisplay;

    public RoleRead(Role role) {
        this.role = role;
        this.roleDisplay = role.getDisplay();
    }
}
