package br.com.manoelegidio.tp4.taskmanager.domain.exception;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
