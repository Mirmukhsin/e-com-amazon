package org.ecomapp.paymentservice.exceptionHandling.customExceptions;

public class UnAuthorizedException extends RuntimeException {
    public UnAuthorizedException(String message) {
        super(message);
    }
}
