package springweb.training_manager.models.validation.exercise_parameters;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = RepetitionOrTimeValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RepetitionOrTimeRequired {
    String DEFAULT_MESSAGE = "Wpisz wartość w polu 'Powtórzenia' lub 'Czas wykonania'";

    String message() default DEFAULT_MESSAGE;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
