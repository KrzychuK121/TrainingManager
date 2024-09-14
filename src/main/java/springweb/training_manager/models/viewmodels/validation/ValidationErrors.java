package springweb.training_manager.models.viewmodels.validation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ValidationErrors {
    private Map<String, List<String>> errors;

    public static ValidationErrors createFrom(BindingResult result) {
        Map<String, List<String>> errors = new HashMap<>();

        result.getFieldErrors().forEach(
            error -> {
                var field = error.getField();
                var message = error.getDefaultMessage();
                if (message == null)
                    message = "Pole nie spełnia wymagań.";

                if (errors.containsKey(field)) {
                    errors.get(field).add(message);
                    return;
                }

                var messages = new ArrayList<>(List.of(message));
                errors.put(error.getField(), messages);
            }
        );

        return new ValidationErrors(errors);
    }
}
