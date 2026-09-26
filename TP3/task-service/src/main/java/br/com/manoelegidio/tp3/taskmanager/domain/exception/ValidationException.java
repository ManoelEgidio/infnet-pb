package br.com.manoelegidio.tp3.taskmanager.domain.exception;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
