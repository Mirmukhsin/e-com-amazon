package org.ecomapp.reviewservice.exceptionHandling.customExceptions;

public class ConflictException extends RuntimeException{
    public ConflictException(String message) {
        super(message);
    }
}
