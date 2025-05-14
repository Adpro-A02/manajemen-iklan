package id.ac.ui.cs.advprog.manajemen_iklan.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidStatusException extends RuntimeException {
    
    public InvalidStatusException(String message) {
        super(message);
    }
    
    public InvalidStatusException(String message, Throwable cause) {
        super(message, cause);
    }
}