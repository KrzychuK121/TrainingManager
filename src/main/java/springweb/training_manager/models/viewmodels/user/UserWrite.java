package springweb.training_manager.models.viewmodels.user;

import jakarta.persistence.Column;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import springweb.training_manager.models.entities.User;
import springweb.training_manager.models.viewmodels.Castable;

@Setter
@Getter
@NoArgsConstructor
public class UserWrite implements Castable<User> {
    @NotBlank(message = "Imie użytkownika nie może być puste.")
    @Length(max = 25)
    protected String firstName;
    @NotBlank(message = "Nazwisko użytkownika nie może być puste.")
    @Length(max = 30)
    protected String lastName;
    @Column(nullable = false, unique = true)
    @Length(min = 8, max = 20, message = "Nazwa użytkownika musi mieć od 8 do 20 znaków.")
    protected String username;
    @Transient
    @Length(min = 8, max = 30, message = "Hasło musi mieć od 8 do 30 znaków.")
    protected String password;
    @Transient
    @NotBlank(message = "Powtórz hasło.")
    private String passwordRepeat;
    private String captchaToken;

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
