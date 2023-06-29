package ru.practicum.stats.validator;

import ru.practicum.stats.model.DateRange;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraintvalidation.SupportedValidationTarget;
import javax.validation.constraintvalidation.ValidationTarget;

@SupportedValidationTarget(ValidationTarget.ANNOTATED_ELEMENT)
public class StartEndValidator implements ConstraintValidator<StartEndDate, DateRange> {

    @Override
    public boolean isValid(DateRange dateRange, ConstraintValidatorContext constraintValidatorContext) {
        return dateRange.getStart().isBefore(dateRange.getEnd()) &&
                !dateRange.getStart().equals(dateRange.getEnd());
    }
}
