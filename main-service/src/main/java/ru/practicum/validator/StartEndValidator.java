package ru.practicum.validator;

import ru.practicum.event.model.EventSearchParams;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraintvalidation.SupportedValidationTarget;
import javax.validation.constraintvalidation.ValidationTarget;

@SupportedValidationTarget(ValidationTarget.ANNOTATED_ELEMENT)
public class StartEndValidator implements ConstraintValidator<StartEndDate, EventSearchParams> {

    @Override
    public boolean isValid(EventSearchParams eventSearchParams, ConstraintValidatorContext constraintValidatorContext) {
        return eventSearchParams.getRangeStart().isBefore(eventSearchParams.getRangeEnd()) &&
                !eventSearchParams.getRangeStart().equals(eventSearchParams.getRangeEnd());
    }
}
