package org.ecomapp.cartservice.exceptionHandling.customExceptions;

public class UnAuthorizedException extends RuntimeException {
    public UnAuthorizedException(String message) {
        super(message);
    }
}
