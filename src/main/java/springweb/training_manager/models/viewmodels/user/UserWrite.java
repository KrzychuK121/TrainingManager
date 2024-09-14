package springweb.training_manager.models.viewmodels.user;

import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import springweb.training_manager.models.entities.User;
import springweb.training_manager.models.schemas.UserSchema;

@Setter
@Getter
@NoArgsConstructor
public class UserWrite extends UserSchema {
    @Transient
    @NotBlank(message = "Powtórz hasło.")
    private String passwordRepeat;

    public User toUser() {
        User toReturn = new User();

        toReturn.setUsername(username);
        toReturn.setFirstName(firstName);
        toReturn.setLastName(lastName);
        toReturn.setPassword(password);

        return toReturn;
    }
}
