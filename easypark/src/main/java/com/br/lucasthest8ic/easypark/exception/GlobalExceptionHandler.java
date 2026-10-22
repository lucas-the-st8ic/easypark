package com.br.lucasthest8ic.easypark.exception;

import com.br.lucasthest8ic.easypark.exception.exceptions.CantBeNullOrBlankException;
import com.br.lucasthest8ic.easypark.exception.exceptions.ResourceAlreadyExistsException;
import com.br.lucasthest8ic.easypark.exception.exceptions.ResourceNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ApiError> buildResponse(HttpStatus status,
                                                   String message) {
        var body = new ApiError(status.value(), message, LocalDateTime.now());
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException exception) {

        String message = exception.getConstraintViolations()
                .stream()
                .map(vltn -> vltn.getMessage())
                .findFirst()
                .orElse("Erro de validação nos dados informados.");

        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                message,
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);

    }

    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<ApiError> handleTransactionSystemException(TransactionSystemException exception) {
        Throwable cause = exception.getRootCause();
        if (cause instanceof  ConstraintViolationException constraintException) {
            return handleConstraintViolation(constraintException);
        }

        ApiError error = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro de transação no banco de dados.",
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(
            ResourceNotFoundException exception
    ) {
        return buildResponse(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleAlreadyExists(
            ResourceAlreadyExistsException exception
    ) {
        return buildResponse(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(CantBeNullOrBlankException.class)
    public ResponseEntity<ApiError> handleNullOrBlank(
            CantBeNullOrBlankException exception
    ) {
        return buildResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrity(DataIntegrityViolationException ex) {
        return buildResponse(HttpStatus.CONFLICT,
                "Operação não permitida: o registro conflita com dados já existentes.");
    }
}
