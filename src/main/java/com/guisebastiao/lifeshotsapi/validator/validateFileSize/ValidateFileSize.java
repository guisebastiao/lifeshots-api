package com.guisebastiao.lifeshotsapi.validator.validateFileSize;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FileSizeValidator.class)
public @interface ValidateFileSize {
    String message() default "Tamanho de arquivo inválido";
    long max();
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
