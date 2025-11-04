package com.guisebastiao.lifeshotsapi.validator.validateMaxFiles;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;

public class MaxFilesValidator implements ConstraintValidator<ValidateMaxFiles, Object> {

    private int max;

    @Override
    public void initialize(ValidateMaxFiles constraintAnnotation) {
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        switch (value) {
            case null -> {
                return true;
            }

            case MultipartFile file -> {
                return !file.isEmpty();
            }

            case Collection<?> files -> {
                long count = files.stream()
                        .filter(item -> item instanceof MultipartFile file && !file.isEmpty())
                        .count();
                return count <= max;
            }

            default -> {
            }
        }

        return false;
    }
}
