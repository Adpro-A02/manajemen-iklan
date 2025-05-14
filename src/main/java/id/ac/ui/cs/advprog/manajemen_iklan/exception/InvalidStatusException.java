package id.ac.ui.cs.advprog.manajemen_iklan.exception;

public class InvalidStatusException extends RuntimeException {
    public InvalidStatusException(String message) {
        super(message);
    }
    
    public InvalidStatusException(String message, Throwable cause) {
        super(message, cause);
    }
}