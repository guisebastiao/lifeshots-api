package com.guisebastiao.lifeshotsapi.validator.validateFileSize;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.List;

public class FileSizeValidator implements ConstraintValidator<ValidateFileSize, Object> {

    private long max;

    @Override
    public void initialize(ValidateFileSize constraintAnnotation) {
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) return true;

        if (value instanceof MultipartFile file) {
            validateFileSize(file);
            return true;
        }

        if (value instanceof Collection<?> files) {
            files.stream()
                    .flatMap(f -> extractFiles(f).stream())
                    .forEach(this::validateFileSize);
            return true;
        }

        return false;
    }

    private void validateFileSize(MultipartFile file) {
        if (file == null || file.isEmpty()) return;

        if (file.getSize() > max) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "O arquivo excede o tamanho máximo permitido de 5MB.");
        }
    }

    private Collection<MultipartFile> extractFiles(Object item) {
        if (item instanceof MultipartFile file) {
            return List.of(file);
        }

        return java.util.Arrays.stream(item.getClass().getDeclaredFields())
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
