package com.bob.bankapispringapp.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class DateValidator  implements ConstraintValidator<ValidBirthdate, LocalDate> {

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        LocalDate now = LocalDate.now();
        LocalDate hundredYearsAgo = now.minusYears(100);

        if (value.isBefore(hundredYearsAgo) || value.isAfter(now)) {
            return false;
        }

        return true;
    }
}
