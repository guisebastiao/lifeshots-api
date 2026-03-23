package com.guisebastiao.lifeshotsapi.controller.common;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.response.FieldErrorResponse;
import com.guisebastiao.lifeshotsapi.enums.BusinessCode;
import com.guisebastiao.lifeshotsapi.exception.*;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

        DefaultResponse<Void> response = DefaultResponse.error(BusinessCode.VALIDATION_ERROR.getValue(), getMessage("validation.error.message"), fieldErrors);

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<DefaultResponse<Void>> handleValidationException(ValidationException e) {
        DefaultResponse<Void> response = DefaultResponse.error(BusinessCode.VALIDATION_ERROR.getValue(), getMessage("validation.error.message"), e.getDetails());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<DefaultResponse<Void>> handleBadRequestException(BadRequestException e) {
        DefaultResponse<Void> response = DefaultResponse.error(BusinessCode.BAD_REQUEST.getValue(), getMessage(e.getMessage()), e.getDetails());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<DefaultResponse<Void>> handleConflictException(ConflictException e) {
        DefaultResponse<Void> response = DefaultResponse.error(BusinessCode.CONFLICT.getValue(), getMessage(e.getMessage()), e.getDetails());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<DefaultResponse<Void>> handleNotFoundException(NotFoundException e) {
        DefaultResponse<Void> response = DefaultResponse.error(BusinessCode.NOT_FOUND.getValue(), getMessage(e.getMessage()), e.getDetails());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<DefaultResponse<Void>> handleUnauthorizedException(UnauthorizedException e) {
        DefaultResponse<Void> response = DefaultResponse.error(BusinessCode.UNAUTHORIZED.getValue(), getMessage(e.getMessage()), e.getDetails());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<DefaultResponse<Void>> handleAccessDeniedException(AccessDeniedException e) {
        DefaultResponse<Void> response = DefaultResponse.error(BusinessCode.ACCESS_DENIED.getValue(), getMessage(e.getMessage()), e.getDetails());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(FailedDependencyException.class)
    public ResponseEntity<DefaultResponse<Void>> handleFailedDependencyException(FailedDependencyException e) {
        DefaultResponse<Void> response = DefaultResponse.error(BusinessCode.FAILED_DEPENDENCY.getValue(), getMessage(e.getMessage()), e.getDetails());
        return ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY).body(response);
    }


    @ExceptionHandler(PrivateProfileException.class)
    public ResponseEntity<DefaultResponse<Void>> handlePrivateProfileException(PrivateProfileException e) {
        DefaultResponse<Void> response = DefaultResponse.error(BusinessCode.PRIVATE_PROFILE.getValue(), getMessage(e.getMessage()), e.getDetails());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(SessionExpiredException.class)
    public ResponseEntity<DefaultResponse<Void>> handleSessionExpiredException(SessionExpiredException e) {
        DefaultResponse<Void> response = DefaultResponse.error(BusinessCode.SESSION_EXPIRED.getValue(), getMessage(e.getMessage()), e.getDetails());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }


    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<DefaultResponse<Void>> handleBusinessValidationException(UsernameNotFoundException e) {
        List<FieldErrorResponse> fieldErrors = List.of(new FieldErrorResponse("email", e.getMessage()));
        DefaultResponse<Void> response = DefaultResponse.error(BusinessCode.VALIDATION_ERROR.getValue(), getMessage("validation.error.message"), fieldErrors);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<DefaultResponse<Void>> handleBadCredentialsException(BadCredentialsException e) {
        List<FieldErrorResponse> fieldErrors = List.of(new FieldErrorResponse("email", e.getMessage()));
        DefaultResponse<Void> response = DefaultResponse.error(BusinessCode.VALIDATION_ERROR.getValue(), getMessage("validation.error.message"), fieldErrors);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<DefaultResponse<Void>> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ignored) {
        DefaultResponse<Void> response = DefaultResponse.error(BusinessCode.METHOD_NOT_ALLOWED.getValue(), getMessage("global-exception-handler.method-not-allowed"));
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<DefaultResponse<Void>> handleNotFound(NoHandlerFoundException ignored) {
        DefaultResponse<Void> response = DefaultResponse.error(BusinessCode.ROUTE_NOT_FOUND.getValue(), getMessage("global-exception-handler.route-not-found"));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<DefaultResponse<Void>> handleInvalidJson() {
        DefaultResponse<Void> response = DefaultResponse.error(BusinessCode.BAD_REQUEST.getValue(),getMessage("global-exception-handler.invalid-json"));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<DefaultResponse<Void>> handleRuntimeException(RuntimeException e) {
        DefaultResponse<Void> response = DefaultResponse.error(BusinessCode.INTERNAL_SERVER_ERROR.getValue(), getMessage("global-exception-handler.internal-server-error"));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    private String getMessage(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }
}
