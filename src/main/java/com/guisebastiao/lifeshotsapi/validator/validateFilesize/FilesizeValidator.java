package com.guisebastiao.lifeshotsapi.validator.validateFilesize;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class FilesizeValidator implements ConstraintValidator<ValidateFilesize, Object> {
    private long max;

    @Override
    public void initialize(ValidateFilesize constraintAnnotation) {
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) return true;

        if (value instanceof MultipartFile file) {
            return validateFileSize(file);
        }

        if (value instanceof Collection<?> files) {
            files.stream()
                    .flatMap(f -> extractFiles(f).stream())
                    .forEach(this::validateFileSize);
            return true;
        }

        return false;
    }

    private boolean validateFileSize(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return true;
        }

        return file.getSize() <= max;
    }

    private Collection<MultipartFile> extractFiles(Object item) {
        if (item instanceof MultipartFile file) return List.of(file);

        return Arrays.stream(item.getClass().getDeclaredFields())
                .filter(f -> MultipartFile.class.isAssignableFrom(f.getType()))
                .map(f -> {
                    f.setAccessible(true);
                    try {
                        return (MultipartFile) f.get(item);
                    } catch (IllegalAccessException e) {
                        return null;
                    }
                })
                .filter(f -> f != null && !f.isEmpty())
                .toList();
    }
}
