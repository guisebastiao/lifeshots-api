package com.guisebastiao.lifeshotsapi.controller.common;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.FieldErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DefaultResponse<List<FieldErrorResponse>>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        logger.error(e.getMessage());

        List<FieldError> fieldErrors = e.getFieldErrors();

        List<FieldErrorResponse> fieldErrorDTOs = fieldErrors.stream()
                .map(fe -> new FieldErrorResponse(fe.getField(), fe.getDefaultMessage()))
                .toList();

        DefaultResponse<List<FieldErrorResponse>> response = new DefaultResponse<List<FieldErrorResponse>>(false, "Erro de validação", fieldErrorDTOs);
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<DefaultResponse<Void>> handleResponseStatusException(ResponseStatusException e) {
        logger.error(e.getMessage());
        DefaultResponse<Void> response = new DefaultResponse<Void>(false, e.getReason(), null);
        return ResponseEntity.status(e.getStatusCode()).body(response);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<DefaultResponse<Void>> handleBadCredentialsException(BadCredentialsException e) {
        logger.error(e.getMessage());
        DefaultResponse<Void> response = new DefaultResponse<Void>(false, e.getMessage(), null);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<DefaultResponse<Void>> handleNotFoundException(NoHandlerFoundException e) {
        logger.error(e.getMessage());
        DefaultResponse<Void> response = new DefaultResponse<Void>(false, "Rota não encontrada", null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<DefaultResponse<Void>> handleRuntimeException(RuntimeException e) {
        logger.error("unexpected error", e);
        DefaultResponse<Void> response = new DefaultResponse<Void>(false, "Ocorreu um erro inesperado, tente novamente mais tarde", null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
