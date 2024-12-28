package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateAfterValidator implements ConstraintValidator<DateAfter, LocalDate> {

    private LocalDate date;

    @Override
    public void initialize(DateAfter constraintAnnotation) {
        date = LocalDate.parse(
                constraintAnnotation.date(),
                DateTimeFormatter.ISO_DATE
        );
    }

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext context) {
        if (date == null) return true;
        return date.isAfter(this.date);
    }
}
