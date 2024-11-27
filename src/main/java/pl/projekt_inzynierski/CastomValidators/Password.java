package pl.projekt_inzynierski.CastomValidators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordValid.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Password {
    String message() default "Password Invalid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
