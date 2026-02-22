package com.guisebastiao.lifeshotsapi.validator.passwordMatcher;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

public class PasswordValidator implements ConstraintValidator<PasswordMatcher, Object> {

    private String firstFieldName;
    private String secondFieldName;
    private String message;


    @Override
    public void initialize(PasswordMatcher constraintAnnotation) {
        this.firstFieldName = constraintAnnotation.first();
        this.secondFieldName = constraintAnnotation.second();
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) return true;

        Object firstValue = new BeanWrapperImpl(value).getPropertyValue(firstFieldName);
        Object secondValue = new BeanWrapperImpl(value).getPropertyValue(secondFieldName);

        boolean valid = firstValue != null && firstValue.equals(secondValue);

        if (!valid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message)
                    .addPropertyNode(secondFieldName)
                    .addConstraintViolation();
        }

        return valid;
    }
}
