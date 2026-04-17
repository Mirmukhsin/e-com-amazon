package org.ecomapp.reviewservice.exceptionHandling.customExceptions;

public class UnAuthorizedException extends RuntimeException {
    public UnAuthorizedException(String message) {
        super(message);
    }
}
