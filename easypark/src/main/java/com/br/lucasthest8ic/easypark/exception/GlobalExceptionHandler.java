package com.br.lucasthest8ic.easypark.exception;

import com.br.lucasthest8ic.easypark.exception.exceptions.ResourceAlreadyExistsException;
import com.br.lucasthest8ic.easypark.exception.exceptions.ResourceNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ApiError> buildResponse(HttpStatus status,
                                                   String message) {
        var body = new ApiError(status.value(), message, LocalDateTime.now());
        return ResponseEntity.status(status).body(body);
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

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrity(
            DataIntegrityViolationException exception
    ) {
        return buildResponse(HttpStatus.CONFLICT,
                "Operação não permitida: o registro conflita com dados já existentes.");
    }

    // Validação do DTO (@Valid no controller): falha antes de chegar ao service.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception
    ) {
        var message = exception.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .sorted()
                .collect(Collectors.joining("; "));

        return buildResponse(HttpStatus.BAD_REQUEST, message);
    }

    // Validação da entidade (Hibernate, no commit): mesmo formato "campo: mensagem".
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(
            ConstraintViolationException exception
    ) {
        var message = exception.getConstraintViolations()
                .stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .sorted()
                .collect(Collectors.joining("; "));

        if (message.isBlank()) {
            message = "Erro de validação nos dados informados.";
        }

        return buildResponse(HttpStatus.BAD_REQUEST, message);
    }

    // A validação da entidade chega embrulhada aqui, porque acontece no commit.
    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<ApiError> handleTransactionSystem(
            TransactionSystemException exception
    ) {
        if (exception.getRootCause() instanceof ConstraintViolationException constraintException) {
            return handleConstraintViolation(constraintException);
        }

        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro de transação no banco de dados.");
    }
}
