package com.lifeshots.lifeshotsapi.controllers.common;

import com.lifeshots.lifeshotsapi.dtos.DefaultDTO;
import com.lifeshots.lifeshotsapi.dtos.ErrorDTO;
import com.lifeshots.lifeshotsapi.dtos.FieldErrorDTO;
import com.lifeshots.lifeshotsapi.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DefaultDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        List<FieldError> fieldErrors = e.getFieldErrors();

        List<FieldErrorDTO> fieldErrorDTO = fieldErrors.stream()
                .map(fe -> new FieldErrorDTO(fe.getField(), fe.getDefaultMessage()))
                .toList();

        ErrorDTO errorDTO = new ErrorDTO(HttpStatus.UNPROCESSABLE_ENTITY.name(),"Erro de validação", request.getRequestURI(), fieldErrorDTO);

        DefaultDTO response = new DefaultDTO("Desculpe, algo não saiu como planejado", Boolean.FALSE, null, errorDTO, null);
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
    }

    @ExceptionHandler(NicknameAlreadyUsedException.class)
    public ResponseEntity<DefaultDTO> handleNicknameAlreadyUsedException(NicknameAlreadyUsedException e, HttpServletRequest request) {
        FieldErrorDTO fieldErrorDTO = new FieldErrorDTO("nickname", e.getMessage());
        ErrorDTO errorDTO = new ErrorDTO(HttpStatus.CONFLICT.name(), "Erro de validação", request.getRequestURI(), List.of(fieldErrorDTO));
        DefaultDTO response = new DefaultDTO(e.getMessage(), Boolean.FALSE, null, errorDTO, null);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(IncorrectCredentialsException.class)
    public ResponseEntity<DefaultDTO> handleIncorrectCredentialsException(IncorrectCredentialsException e, HttpServletRequest request) {
        FieldErrorDTO emailError = new FieldErrorDTO("email", "Email ou senha incorretos");
        FieldErrorDTO passwordError = new FieldErrorDTO("password", "Senha ou email incorretos");
        ErrorDTO errorDTO = new ErrorDTO(HttpStatus.UNPROCESSABLE_ENTITY.name(), "Erro de validação", request.getRequestURI(), List.of(emailError,  passwordError));
        DefaultDTO response = new DefaultDTO(e.getMessage(), Boolean.FALSE, null, errorDTO, null);
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
    }

    @ExceptionHandler(DuplicateEntityException.class)
    public ResponseEntity<DefaultDTO> handleDuplicateEntityException(DuplicateEntityException e, HttpServletRequest request) {
        ErrorDTO errorDTO = new ErrorDTO(HttpStatus.CONFLICT.name(), e.getMessage(), request.getRequestURI(), null);
        DefaultDTO response = new DefaultDTO(e.getMessage(), Boolean.FALSE, null, errorDTO, null);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<DefaultDTO> handleEntityNotFoundException(EntityNotFoundException e, HttpServletRequest request) {
        ErrorDTO errorDTO = new ErrorDTO(HttpStatus.NOT_FOUND.name(), e.getMessage(), request.getRequestURI(), null);
        DefaultDTO response = new DefaultDTO("Desculpe, algo não saiu como planejado", Boolean.FALSE, null, errorDTO, null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(PasswordIncorrectException.class)
    public ResponseEntity<DefaultDTO> handlePasswordIncorrectException(PasswordIncorrectException e, HttpServletRequest request) {
        FieldErrorDTO fieldErrorDTO = new FieldErrorDTO(e.getField(), e.getMessage());
        ErrorDTO errorDTO = new ErrorDTO(HttpStatus.UNPROCESSABLE_ENTITY.name(), "Erro de validação", request.getRequestURI(), List.of(fieldErrorDTO));
        DefaultDTO response = new DefaultDTO("Desculpe, algo não saiu como planejado", Boolean.FALSE, null, errorDTO, null);
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<DefaultDTO> handleBadRequestException(BadRequestException e, HttpServletRequest request) {
        ErrorDTO errorDTO = new ErrorDTO(HttpStatus.BAD_REQUEST.name(), e.getMessage(), request.getRequestURI(), null);
        DefaultDTO response = new DefaultDTO("Desculpe, algo não saiu como planejado", Boolean.FALSE, null, errorDTO, null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<DefaultDTO> handleUnauthorizedException(Exception e, HttpServletRequest request) {
        ErrorDTO errorDTO = new ErrorDTO(HttpStatus.UNAUTHORIZED.name(), e.getMessage(), request.getRequestURI(), null);
        DefaultDTO response = new DefaultDTO("Desculpe, algo não saiu como planejado", Boolean.FALSE, null, errorDTO, null);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<DefaultDTO> handleNotFound(HttpServletRequest request) {
        ErrorDTO errorDTO = new ErrorDTO(HttpStatus.NOT_FOUND.name(), "URI não foi encontrada", request.getRequestURI(), null);
        DefaultDTO response = new DefaultDTO("Desculpe, algo não saiu como planejado", Boolean.FALSE, null, errorDTO, null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(FailedUploadFileException.class)
    public ResponseEntity<DefaultDTO> handleFailedUploadFileException(Exception e, HttpServletRequest request) {
        ErrorDTO errorDTO = new ErrorDTO(HttpStatus.INTERNAL_SERVER_ERROR.name(), e.getMessage(), request.getRequestURI(), null);
        DefaultDTO response = new DefaultDTO("Desculpe, algo não saiu como planejado", Boolean.FALSE, null, errorDTO, null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<DefaultDTO> handleRuntimeException(HttpServletRequest request) {
        ErrorDTO errorDTO = new ErrorDTO(HttpStatus.INTERNAL_SERVER_ERROR.name(), "Erro interno do servidor. Tente novamente mais tarde", request.getRequestURI(), null);
        DefaultDTO response = new DefaultDTO("Desculpe, algo não saiu como planejado", Boolean.FALSE, null, errorDTO, null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}