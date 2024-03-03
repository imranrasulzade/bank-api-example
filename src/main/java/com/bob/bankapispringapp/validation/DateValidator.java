package com.bob.bankapispringapp.validation;

import com.bob.bankapispringapp.exception.InvalidBirthdateException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class DateValidator  implements ConstraintValidator<ValidBirthdate, LocalDate> {

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            throw new InvalidBirthdateException("Invalid date for birthdate");
        }

        LocalDate now = LocalDate.now();
        LocalDate hundredYearsAgo = now.minusYears(100);

        if (value.isBefore(hundredYearsAgo)) {
            throw new InvalidBirthdateException("Invalid date for birthdate");
        }
        if(value.isAfter(now)){
            throw new InvalidBirthdateException("Invalid date for birthdate");
        }

        return true;
    }
}
