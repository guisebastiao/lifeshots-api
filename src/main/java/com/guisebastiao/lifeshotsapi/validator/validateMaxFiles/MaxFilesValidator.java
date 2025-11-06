package com.guisebastiao.lifeshotsapi.validator.validateMaxFiles;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Field;
import java.util.Collection;

public class MaxFilesValidator implements ConstraintValidator<ValidateMaxFiles, Object> {

    private int max;

    @Override
    public void initialize(ValidateMaxFiles constraintAnnotation) {
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) return true;

        if (value instanceof Collection<?> files) {
            long count = files.stream()
                    .filter(this::isValidFile)
                    .count();
            return count <= max;
        }

        if (value instanceof MultipartFile file) {
            return !file.isEmpty();
        }

        return false;
    }

    private boolean isValidFile(Object item) {
        if (item == null) return false;

        if (item instanceof MultipartFile file) {
            return !file.isEmpty();
        }

        for (Field field : item.getClass().getDeclaredFields()) {
            if (MultipartFile.class.isAssignableFrom(field.getType())) {
                field.setAccessible(true);

                try {
                    MultipartFile file = (MultipartFile) field.get(item);
                    if (file != null && !file.isEmpty()) {
                        return true;
                    }
                } catch (IllegalAccessException ignored) {}
            }
        }

        return false;
    }
}
