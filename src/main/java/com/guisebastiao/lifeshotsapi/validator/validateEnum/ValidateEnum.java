package com.guisebastiao.lifeshotsapi.validator.validateEnum;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class ValidateEnum implements ConstraintValidator<EnumValidator, String> {
    private Class<? extends Enum<?>> enumClass;

    @Override
    public void initialize(EnumValidator annotation) {
        this.enumClass = annotation.enumClass();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;

        return Arrays.stream(enumClass.getEnumConstants())
                .anyMatch(e -> e.name().equalsIgnoreCase(value));
    }
}
