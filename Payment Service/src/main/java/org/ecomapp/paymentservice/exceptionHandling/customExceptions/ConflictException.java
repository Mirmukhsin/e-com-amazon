package org.ecomapp.paymentservice.exceptionHandling.customExceptions;

public class ConflictException extends RuntimeException{
    public ConflictException(String message) {
        super(message);
    }
}
