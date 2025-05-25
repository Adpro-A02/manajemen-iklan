package id.ac.ui.cs.advprog.manajemen_iklan.exception;

import id.ac.ui.cs.advprog.manajemen_iklan.dto.IklanResponseDTO;
import id.ac.ui.cs.advprog.manajemen_iklan.utils.ResponseFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private ResponseFactory responseFactory;

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    private IklanResponseDTO mockResponse;

    @BeforeEach
    void setUp() {
        mockResponse = IklanResponseDTO.builder()
                .code(HttpStatus.NOT_FOUND.value())
                .success(false)
                .message("Error occurred")
                .build();
    }

    @Test
    void testHandleResourceNotFoundException() {
        // Given
        ResourceNotFoundException exception = new ResourceNotFoundException("Resource not found");
        when(responseFactory.createErrorResponse(HttpStatus.NOT_FOUND.value(), "Resource not found"))
                .thenReturn(mockResponse);

        // When
        ResponseEntity<IklanResponseDTO> response = exceptionHandler.handleResourceNotFoundException(exception);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
        
        verify(responseFactory).createErrorResponse(HttpStatus.NOT_FOUND.value(), "Resource not found");
    }

    @Test
    void testHandleIllegalArgumentException() {
        // Given
        IllegalArgumentException exception = new IllegalArgumentException("Invalid argument");
        when(responseFactory.createErrorResponse(HttpStatus.BAD_REQUEST.value(), "Invalid argument"))
                .thenReturn(mockResponse);

        // When
        ResponseEntity<IklanResponseDTO> response = exceptionHandler.handleIllegalArgumentException(exception);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
        
        verify(responseFactory).createErrorResponse(HttpStatus.BAD_REQUEST.value(), "Invalid argument");
    }

    @Test
    void testHandleValidationExceptions() {
        // Given
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("iklanDTO", "title", "Title is required");
        
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));
        when(responseFactory.createCustomResponse(any(), eq(HttpStatus.BAD_REQUEST.value()), eq("Validation failed")))
                .thenReturn(mockResponse);

        // When
        ResponseEntity<IklanResponseDTO> response = exceptionHandler.handleValidationExceptions(exception);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
        
        verify(responseFactory).createCustomResponse(any(), eq(HttpStatus.BAD_REQUEST.value()), eq("Validation failed"));
    }

    @Test
    void testHandleGenericException() {
        // Given
        Exception exception = new Exception("Unexpected error");
        when(responseFactory.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred"))
                .thenReturn(mockResponse);

        // When
        ResponseEntity<IklanResponseDTO> response = exceptionHandler.handleGenericException(exception);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
        
        verify(responseFactory).createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred");
    }
}