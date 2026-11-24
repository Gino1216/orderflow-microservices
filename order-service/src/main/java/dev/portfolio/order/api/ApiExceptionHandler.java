package dev.portfolio.order.api;

import dev.portfolio.order.application.OrderNotFoundException;
import dev.portfolio.order.catalog.CatalogUnavailableException;
import dev.portfolio.order.catalog.CatalogReservationException;
import jakarta.validation.ConstraintViolationException;
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

    @ExceptionHandler(OrderNotFoundException.class)
    ResponseEntity<ErrorResponse> notFound(OrderNotFoundException exception) {
        return response(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(CatalogUnavailableException.class)
    ResponseEntity<ErrorResponse> catalogError(CatalogUnavailableException exception) {
        return response(HttpStatus.SERVICE_UNAVAILABLE, exception.getMessage());
    }

    @ExceptionHandler(CatalogReservationException.class)
    ResponseEntity<ErrorResponse> reservationError(CatalogReservationException exception) {
        return response(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
    ResponseEntity<ErrorResponse> invalid(Exception exception) {
        return response(HttpStatus.BAD_REQUEST, "Request validation failed");
    }

    private ResponseEntity<ErrorResponse> response(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(new ErrorResponse(Instant.now(), status.value(), message, Map.of()));
    }
}
