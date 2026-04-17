package org.ecomapp.paymentservice.exceptionHandling.customExceptions;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
