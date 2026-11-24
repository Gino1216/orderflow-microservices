package dev.portfolio.catalog.common;

import dev.portfolio.catalog.product.InsufficientStockException;
import dev.portfolio.catalog.product.ProductNotFoundException;
import java.time.Instant;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    public record ErrorResponse(Instant timestamp, int status, String message, Map<String, String> errors) {
    }

    @ExceptionHandler(ProductNotFoundException.class)
    ResponseEntity<ErrorResponse> notFound(ProductNotFoundException exception) {
        return response(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(InsufficientStockException.class)
    ResponseEntity<ErrorResponse> conflict(InsufficientStockException exception) {
        return response(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponse> invalid(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .findFirst().map(error -> error.getField() + " " + error.getDefaultMessage())
                .orElse("Invalid request");
        return response(HttpStatus.BAD_REQUEST, message);
    }

    private ResponseEntity<ErrorResponse> response(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(new ErrorResponse(Instant.now(), status.value(), message, Map.of()));
    }
}
