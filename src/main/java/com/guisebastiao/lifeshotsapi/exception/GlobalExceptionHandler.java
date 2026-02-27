package com.guisebastiao.lifeshotsapi.exception;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.response.FieldErrorResponse;
import com.guisebastiao.lifeshotsapi.enums.BusinessHttpStatus;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
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

        DefaultResponse<Void> response = DefaultResponse.error(BusinessHttpStatus.VALIDATION_ERROR.getCode(), getMessage("global-exception-handler.unprocessable-entity"), fieldErrors);

        return ResponseEntity.status(BusinessHttpStatus.VALIDATION_ERROR.getHttpStatus()).body(response);
    }

    // UsernameNotFoundException - criar exception, de CustomUserDetailsService

    @ExceptionHandler(BusinessValidationException.class)
    public ResponseEntity<DefaultResponse<Void>> handleBusinessValidationException(BusinessValidationException e) {
        List<FieldErrorResponse> fieldErrors = List.of(new FieldErrorResponse(e.getField(), e.getMessage()));
        DefaultResponse<Void> response = DefaultResponse.error(e.getStatus().getCode(), getMessage("global-exception-handler.unprocessable-entity"), fieldErrors);
        return ResponseEntity.status(e.getStatus().getHttpStatus()).body(response);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<DefaultResponse<Void>> handleBadCredentialsException(BadCredentialsException e) {
        List<FieldErrorResponse> fieldErrors = List.of(new FieldErrorResponse("errorCredentials", e.getMessage()));
        DefaultResponse<Void> response = DefaultResponse.error(BusinessHttpStatus.BAD_CREDENTIALS.getCode(), getMessage("global-exception-handler.bad-credentials"), fieldErrors);
        return ResponseEntity.status(BusinessHttpStatus.BAD_CREDENTIALS.getHttpStatus()).body(response);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<DefaultResponse<Void>> handleAuthorizationDeniedException(AuthorizationDeniedException ignored) {
        return ResponseEntity.status(BusinessHttpStatus.APPLICATION_ACCESS_DENIED.getHttpStatus()).body(DefaultResponse.error(BusinessHttpStatus.APPLICATION_ACCESS_DENIED.getCode(), getMessage("global-exception-handler.authorization-denied-exception")));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<DefaultResponse<Void>> handleResponseStatusException(BusinessException e) {
        DefaultResponse<Void> response = DefaultResponse.error(e.getStatus().getCode(), e.getMessage());
        return ResponseEntity.status(e.getStatus().getHttpStatus()).body(response);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<DefaultResponse<Void>> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ignored) {
        DefaultResponse<Void> response = DefaultResponse.error(BusinessHttpStatus.METHOD_NOT_ALLOWED.getCode(), getMessage("global-exception-handler.method-not-allowed"));
        return ResponseEntity.status(BusinessHttpStatus.METHOD_NOT_ALLOWED.getHttpStatus()).body(response);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<DefaultResponse<Void>> handleNotFound(NoHandlerFoundException ignored) {
        DefaultResponse<Void> response = DefaultResponse.error(BusinessHttpStatus.ROUTE_NOT_FOUND.getCode(), getMessage("global-exception-handler.route-not-found"));
        return ResponseEntity.status(BusinessHttpStatus.ROUTE_NOT_FOUND.getHttpStatus()).body(response);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<DefaultResponse<Void>> handleRuntimeException(RuntimeException e) {
        DefaultResponse<Void> response = DefaultResponse.error(BusinessHttpStatus.INTERNAL_SERVER_ERROR.getCode(), getMessage("global-exception-handler.internal-server-error"));
        return ResponseEntity.status(BusinessHttpStatus.INTERNAL_SERVER_ERROR.getHttpStatus()).body(response);
    }

    private String getMessage(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }
}
