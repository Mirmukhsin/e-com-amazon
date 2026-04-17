package org.ecomapp.cartservice.exceptionHandling;

import jakarta.servlet.http.HttpServletRequest;
import org.ecomapp.cartservice.exceptionHandling.customExceptions.BadRequestException;
import org.ecomapp.cartservice.exceptionHandling.customExceptions.ConflictException;
import org.ecomapp.cartservice.exceptionHandling.customExceptions.ResourceNotFoundException;
import org.ecomapp.cartservice.exceptionHandling.customExceptions.UnAuthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
//    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponseDTO> handleNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .title("Resource not found")
                .status(404)
                .detail(ex.getMessage())
                .path(req.getRequestURI())
                .timestamp(Instant.now())
                .build();
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneric(Exception ex, HttpServletRequest req) {
        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .title("Internal Server Error")
                .status(500)
                .detail(ex.getMessage())
                .path(req.getRequestURI())
                .timestamp(Instant.now())
                .build();
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponseDTO> handleConflict(ConflictException ex, HttpServletRequest req) {
        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .title("Conflict error")
                .status(409)
                .detail(ex.getMessage())
                .path(req.getRequestURI())
                .timestamp(Instant.now())
                .build();
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponseDTO> handleForbidden(BadRequestException ex, HttpServletRequest req) {
        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .title("Bad request")
                .status(400)
                .detail(ex.getMessage())
                .path(req.getRequestURI())
                .timestamp(Instant.now())
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnAuthorizedException.class)
    public ResponseEntity<ErrorResponseDTO> handleUnAuthorized(UnAuthorizedException ex, HttpServletRequest req) {
        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .title("Unauthorized")
                .status(401)
                .detail(ex.getMessage())
                .path(req.getRequestURI())
                .timestamp(Instant.now())
                .build();
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {

        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .findFirst().orElse("Invalid input");

        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .title("Validation error")
                .status(400)
                .detail(message)
                .path(req.getRequestURI())
                .timestamp(Instant.now())
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
