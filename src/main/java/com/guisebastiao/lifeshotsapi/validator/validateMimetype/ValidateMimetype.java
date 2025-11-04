package com.guisebastiao.lifeshotsapi.validator.validateMimetype;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MimetypeValidator.class)
public @interface ValidateMimetype {
    String[] allowed();
    String message() default "Tipo de arquivo não suportado";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
