package com.guisebastiao.lifeshotsapi.validator.validateMimetype;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class MimetypeValidator implements ConstraintValidator<ValidateMimetype, Object> {
    private List<String> allowedTypes;

    @Override
    public void initialize(ValidateMimetype constraintAnnotation) {
        this.allowedTypes = Arrays.asList(constraintAnnotation.allowed());
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) return true;

        if (value instanceof MultipartFile file) {
            return validateType(file);
        }

        if (value instanceof Collection<?> files) {
            files.stream()
                    .flatMap(f -> extractFiles(f).stream())
                    .forEach(this::validateType);
            return true;
        }

        return false;
    }

    private boolean validateType(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return true;
        }

        String type = file.getContentType();

        return type != null && allowedTypes.contains(type);
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