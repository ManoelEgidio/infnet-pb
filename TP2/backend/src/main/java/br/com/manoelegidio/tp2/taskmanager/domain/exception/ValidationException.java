package br.com.manoelegidio.tp2.taskmanager.domain.exception;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
