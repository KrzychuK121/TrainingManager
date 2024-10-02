package springweb.training_manager.models.viewmodels.user;

import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import springweb.training_manager.models.entities.User;
import springweb.training_manager.models.schemas.UserSchema;
import springweb.training_manager.models.viewmodels.Castable;

@Setter
@Getter
@NoArgsConstructor
public class UserWrite extends UserSchema implements Castable<User> {
    @Transient
    @NotBlank(message = "Powtórz hasło.")
    private String passwordRepeat;

    @Override
    public User toEntity() {
        User toReturn = new User(
            firstName,
            lastName,
            username,
            password
        );

        return toReturn;
    }
}
