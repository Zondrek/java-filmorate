package ru.yandex.practicum.filmorate.validation.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {DateAfterValidator.class})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface DateAfter {

    String message() default "Дата должна быть после указанной даты";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Паттерн: ISO_DATE yyyy-MM-dd
     */
    String value();
}
