package id.ac.ui.cs.advprog.manajemen_iklan.exception;

import id.ac.ui.cs.advprog.manajemen_iklan.dto.IklanResponseDTO;
import id.ac.ui.cs.advprog.manajemen_iklan.utils.ResponseFactory;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final ResponseFactory responseFactory;

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<IklanResponseDTO> handleResourceNotFoundException(ResourceNotFoundException ex) {
        logger.error("Resource not found: {}", ex.getMessage());
        IklanResponseDTO response = responseFactory.createErrorResponse(
            HttpStatus.NOT_FOUND.value(), 
            ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<IklanResponseDTO> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        logger.error("Validation failed: {}", errors);
        IklanResponseDTO response = responseFactory.createCustomResponse(
            errors,
            HttpStatus.BAD_REQUEST.value(),
            "Validation failed"
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<IklanResponseDTO> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.error("Invalid argument: {}", ex.getMessage());
        IklanResponseDTO response = responseFactory.createErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<IklanResponseDTO> handleGenericException(Exception ex) {
        logger.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        IklanResponseDTO response = responseFactory.createErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "An unexpected error occurred"
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}