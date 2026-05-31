package com.awbd.pawsy.adoption.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class AdoptionExceptionHandler {

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> handleIllegalStateException(
            IllegalStateException ex) {

        var error = Map.of(
                "error", "ILLEGAL_STATE",
                "message", ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleEntityNotFoundException(
            EntityNotFoundException ex) {

        var error = Map.of(
                "error", "ENTITY_NOT_FOUND",
                "message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception ex) {
        var error = Map.of(
                "error", "INTERNAL_ERROR",
                "message", ex.getMessage());
        return ResponseEntity.internalServerError().body(error);
    }
}
