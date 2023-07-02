package ru.practicum.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class BeforeValidator implements ConstraintValidator<Before, LocalDateTime> {

    @Override
    public boolean isValid(LocalDateTime localDateTime, ConstraintValidatorContext context) {
        return localDateTime.isBefore(LocalDateTime.now().plusHours(2));
    }
}

