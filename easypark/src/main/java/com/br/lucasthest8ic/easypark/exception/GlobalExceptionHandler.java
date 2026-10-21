package com.br.lucasthest8ic.easypark.exception;

import com.br.lucasthest8ic.easypark.exception.exceptions.CantBeNullOrBlankException;
import com.br.lucasthest8ic.easypark.exception.exceptions.ResourceAlreadyExistsException;
import com.br.lucasthest8ic.easypark.exception.exceptions.ResourceNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ApiError> buildResponse(HttpStatus status,
                                                   String message)
    {
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
