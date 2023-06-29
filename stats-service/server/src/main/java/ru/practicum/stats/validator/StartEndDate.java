package ru.practicum.stats.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = StartEndValidator.class)
@Target({TYPE})
@Retention(RUNTIME)
@Documented
public @interface StartEndDate {

    String message() default "StartDate must be before AndDate";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}