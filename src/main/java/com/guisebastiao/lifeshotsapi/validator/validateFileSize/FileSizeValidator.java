package com.guisebastiao.lifeshotsapi.validator.validateFileSize;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

public class FileSizeValidator implements ConstraintValidator<ValidateFileSize, Object> {
    private long max;

    @Override
    public void initialize(ValidateFileSize constraintAnnotation) {
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return switch (value) {
            case null -> true;
            case MultipartFile file -> {
                if (file.getSize() > max) {
                    throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "O arquivo excede o tamanho máximo permitido de 5MB.");
                }

                yield true;
            }
            case List<?> list -> {
                boolean tooLarge = list.stream()
                        .filter(item -> item instanceof MultipartFile)
                        .map(item -> (MultipartFile) item)
                        .anyMatch(file -> file.getSize() > max);

                if (tooLarge) {
                    throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "Um ou mais arquivos excedem o tamanho máximo permitido de 5MB.");
                }

                yield true;
            }
            default -> false;
        };
    }
}
