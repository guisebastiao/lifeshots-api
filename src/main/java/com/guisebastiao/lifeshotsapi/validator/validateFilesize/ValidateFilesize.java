package com.guisebastiao.lifeshotsapi.validator.validateFilesize;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FilesizeValidator.class)
public @interface ValidateFilesize {
    String message() default "The file exceeds the maximum limit.";
    long max();
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
