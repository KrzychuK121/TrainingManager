package springweb.training_manager.models.view_models.authentication;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import springweb.training_manager.models.entities.Role;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private String firstName;
    private String lastName;
    private Role role;
}
