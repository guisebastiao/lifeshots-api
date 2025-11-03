package com.guisebastiao.lifeshotsapi.validator.ValidateMimetype;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

public class MimetypeValidator implements ConstraintValidator<ValidateMimetype, Object> {

    private List<String> allowedTypes;

    @Override
    public void initialize(ValidateMimetype constraintAnnotation) {
        this.allowedTypes = Arrays.asList(constraintAnnotation.allowed());
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return switch (value) {
            case null -> true;

            case MultipartFile file -> {
                if (!allowedTypes.contains(file.getContentType())) {
                    throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, String.format("Tipo de arquivo '%s' não suportado. Envie uma imagem nos formatos: %s.",  file.getContentType(), String.join(", ", allowedTypes)));
                }

                yield true;
            }

            case List<?> list -> {
                boolean hasInvalid = list.stream()
                        .filter(item -> item instanceof MultipartFile)
                        .map(item -> (MultipartFile) item)
                        .anyMatch(file -> !allowedTypes.contains(file.getContentType()));

                if (hasInvalid) {
                    throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, String.format("Um ou mais arquivos possuem tipo não suportado. Tipos permitidos: %s.", String.join(", ", allowedTypes)));
                }

                yield true;
            }

            default -> false;
        };
    }
}