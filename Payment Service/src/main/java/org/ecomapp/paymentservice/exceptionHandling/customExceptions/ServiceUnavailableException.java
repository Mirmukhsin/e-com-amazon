package org.ecomapp.paymentservice.exceptionHandling.customExceptions;

public class ServiceUnavailableException extends RuntimeException {
    public ServiceUnavailableException(String message) {
        super(message);
    }
}
