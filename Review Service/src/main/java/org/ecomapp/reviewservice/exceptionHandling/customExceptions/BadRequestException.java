package org.ecomapp.reviewservice.exceptionHandling.customExceptions;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
