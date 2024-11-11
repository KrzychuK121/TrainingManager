package springweb.training_manager.models.validation.exercise_parameters;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import springweb.training_manager.models.schemas.ExerciseParametersSchema;

public class RepetitionOrTimeValidator implements ConstraintValidator<
    RepetitionOrTimeRequired,
    ExerciseParametersSchema
    > {

    @Override
    public boolean isValid(
        ExerciseParametersSchema parameters,
        ConstraintValidatorContext context
    ) {
        context.disableDefaultConstraintViolation();

        if (
            parameters.getRepetition() == 0
                && parameters.getTime() == null) {

            context.buildConstraintViolationWithTemplate(
                    RepetitionOrTimeRequired.DEFAULT_MESSAGE
                )
                .addPropertyNode("repetition")
                .addConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    RepetitionOrTimeRequired.DEFAULT_MESSAGE
                )
                .addPropertyNode("time")
                .addConstraintViolation();

            return false;
        }

        return true;
    }
}
