package br.com.manoelegidio.tp1.taskmanager.domain.exception;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
