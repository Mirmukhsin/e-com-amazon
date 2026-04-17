package org.ecomapp.orderservice.exceptionHandling.customExceptions;

public class UnAuthorizedException extends RuntimeException {
    public UnAuthorizedException(String message) {
        super(message);
    }
}
