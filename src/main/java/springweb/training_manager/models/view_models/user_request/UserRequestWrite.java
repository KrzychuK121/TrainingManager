package springweb.training_manager.models.view_models.user_request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@AllArgsConstructor
@Setter
@Getter
public class UserRequestWrite {
    @NotNull(message = "Tytuł nie może być pusty")
    @Length(min = 5, max = 100, message = "Tytuł musi zawierać od 5 do 100 znaków")
    private String title;
    @NotNull(message = "Opis nie może być pusty")
    @NotBlank(message = "Opis nie może być pusty")
    private String description;
}
