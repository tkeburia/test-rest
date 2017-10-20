package com.tk.testRest.annotation.validator;

import com.tk.testRest.annotation.CustomConstraint;
import com.tk.testRest.dto.EnumActionValues;

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
