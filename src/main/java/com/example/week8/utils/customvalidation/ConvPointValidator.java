package com.example.week8.utils.customvalidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ConvPointValidator implements ConstraintValidator<ConvPointCheck, Integer> {
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return value != null && value >= 1000;
    }
}
