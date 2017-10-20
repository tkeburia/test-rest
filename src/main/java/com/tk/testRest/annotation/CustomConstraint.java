package com.tk.testRest.annotation;

import com.tk.testRest.annotation.validator.CustomConstraintValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CustomConstraintValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD } )
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomConstraint
{
    String message() default "Invalid enum";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
