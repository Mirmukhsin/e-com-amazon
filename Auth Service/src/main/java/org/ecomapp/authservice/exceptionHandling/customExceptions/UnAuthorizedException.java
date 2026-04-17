package org.ecomapp.authservice.exceptionHandling.customExceptions;

public class UnAuthorizedException extends RuntimeException {
    public UnAuthorizedException(String message) {
        super(message);
    }
}
