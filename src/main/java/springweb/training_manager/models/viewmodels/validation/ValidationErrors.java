package springweb.training_manager.models.viewmodels.validation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.BindingResult;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ValidationErrors {
    private Map<String, String> errors;

    public static ValidationErrors createFrom(BindingResult result) {
        Map<String, String> errors = new HashMap<>();

        result.getFieldErrors().forEach(
            error -> errors.put(error.getField(), error.getDefaultMessage())
        );

        return new ValidationErrors(errors);
    }
}
