package com.guisebastiao.lifeshotsapi.exception;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.response.FieldErrorResponse;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DefaultResponse<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<FieldErrorResponse> fieldErrors = e.getBindingResult().getAllErrors().stream()
                .map(error -> {
                    String fieldName = error instanceof FieldError fieldError ? fieldError.getField() : error.getObjectName();
                    String message = error.getDefaultMessage();
                    return new FieldErrorResponse(fieldName, message);
                })
                .toList();

        DefaultResponse<Void> response = DefaultResponse.error("VALIDATION_ERROR", getMessage("global-exception-handler.unprocessable-entity"), fieldErrors);

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
    }

    @ExceptionHandler(BusinessValidationException.class)
    public ResponseEntity<DefaultResponse<Void>> handleBusinessValidationException(BusinessValidationException e) {
        List<FieldErrorResponse> fieldErrors = List.of(new FieldErrorResponse(e.getField(), e.getMessage()));
        DefaultResponse<Void> response = DefaultResponse.error(e.getErrorStatus(), getMessage("global-exception-handler.unprocessable-entity"), fieldErrors);
        return ResponseEntity.status(e.getStatus()).body(response);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<DefaultResponse<Void>> handleBadCredentialsException(BadCredentialsException e) {
        List<FieldErrorResponse> fieldErrors = List.of(new FieldErrorResponse("errorCredentials", e.getMessage()));
        DefaultResponse<Void> response = DefaultResponse.error("BAD_CREDENTIALS", getMessage("global-exception-handler.bad-credentials"), fieldErrors);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<DefaultResponse<Void>> handleAuthorizationDeniedException(AuthorizationDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(DefaultResponse.error(HttpStatus.FORBIDDEN.name(), getMessage("global-exception-handler.authorization-denied-exception")));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<DefaultResponse<Void>> handleResponseStatusException(ResponseStatusException e) {
        DefaultResponse<Void> response = DefaultResponse.error(HttpStatus.valueOf(e.getStatusCode().value()).name(), e.getReason());
        return ResponseEntity.status(e.getStatusCode()).body(response);
    }

    @ExceptionHandler(BusinessTokenInvalidException.class)
    public ResponseEntity<DefaultResponse<Void>> handleMethodNotAllowed(BusinessTokenInvalidException ex) {
        DefaultResponse<Void> response = DefaultResponse.error("TOKEN_EXPIRED", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<DefaultResponse<Void>> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        DefaultResponse<Void> response = DefaultResponse.error(HttpStatus.METHOD_NOT_ALLOWED.name(), getMessage("global-exception-handler.method-not-allowed"));
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<DefaultResponse<Void>> handleNotFound(NoHandlerFoundException ex) {
        DefaultResponse<Void> response = DefaultResponse.error("ROUTE_NOT_FOUND", getMessage("global-exception-handler.route-not-found"));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<DefaultResponse<Void>> handleRuntimeException(RuntimeException e) {
        DefaultResponse<Void> response = DefaultResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.name(), getMessage("global-exception-handler.internal-server-error"));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    private String getMessage(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }
}
