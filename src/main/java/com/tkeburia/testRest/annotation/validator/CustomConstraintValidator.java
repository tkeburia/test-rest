package com.tkeburia.testRest.annotation.validator;

import com.tkeburia.testRest.annotation.CustomConstraint;
import com.tkeburia.testRest.dto.EnumActionValues;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CustomConstraintValidator implements ConstraintValidator<CustomConstraint, EnumActionValues> {
    @Override
    public void initialize(CustomConstraint constraint) {
    }

    @Override
    public boolean isValid(EnumActionValues obj, ConstraintValidatorContext context) {
        return obj == null || obj.getAllowedActions().contains(obj);
    }
}
