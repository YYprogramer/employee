package com.yy5.employee.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AgeValidator implements ConstraintValidator<ValidEmployeeAge, String> {
    @Override
    public void initialize(ValidEmployeeAge constraintAnnotation) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context){
        return value != null && value.trim().length() > 0;
    }
}
