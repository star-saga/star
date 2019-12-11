package org.event.driven.light.kafkaserialize.exception;

public class LightException extends RuntimeException {
    public LightException(String message) {
        super(message);
    }

    public LightException(String cause, Throwable throwable) {
        super(cause, throwable);
    }
}
