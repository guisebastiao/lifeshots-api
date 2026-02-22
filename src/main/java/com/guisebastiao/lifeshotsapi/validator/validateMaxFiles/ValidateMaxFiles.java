package com.guisebastiao.lifeshotsapi.validator.validateMaxFiles;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MaxFilesValidator.class)
public @interface ValidateMaxFiles {
    String message() default "Maximum number of files exceeded.";
    int max();
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}