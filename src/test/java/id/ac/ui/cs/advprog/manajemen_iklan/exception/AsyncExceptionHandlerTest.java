package id.ac.ui.cs.advprog.manajemen_iklan.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AsyncExceptionHandlerTest {

    private AsyncExceptionHandler asyncExceptionHandler;

    @BeforeEach
    void setUp() {
        asyncExceptionHandler = new AsyncExceptionHandler();
    }

    @Test
    void testHandleUncaughtException_GenericException() throws NoSuchMethodException {
        // Given
        Exception exception = new Exception("Test exception");
        Method method = this.getClass().getMethod("testMethod");
        Object[] params = {"param1", "param2"};

        // When & Then - Should not throw any exception
        assertDoesNotThrow(() -> {
            asyncExceptionHandler.handleUncaughtException(exception, method, params);
        });
    }

    @Test
    void testHandleUncaughtException_WithNullException() throws NoSuchMethodException {
        // Given
        Method method = this.getClass().getMethod("testMethod");
        Object[] params = {"param1"};

        // When & Then - Should handle null exception gracefully with fixed implementation
        assertDoesNotThrow(() -> {
            asyncExceptionHandler.handleUncaughtException(null, method, params);
        });
    }

    @Test
    void testHandleUncaughtException_WithNullMethod() {
        // Given
        Exception exception = new Exception("Test exception");
        Object[] params = {"param1"};

        // When & Then - Should handle null method gracefully with fixed implementation
        assertDoesNotThrow(() -> {
            asyncExceptionHandler.handleUncaughtException(exception, null, params);
        });
    }

    @Test
    void testHandleUncaughtException_WithAllNullParameters() {
        // When & Then - Should handle all null parameters gracefully with fixed implementation
        assertDoesNotThrow(() -> {
            asyncExceptionHandler.handleUncaughtException(null, null, (Object[]) null);
        });
    }

    @Test
    void testHandleUncaughtException_WithResourceNotFoundException() throws NoSuchMethodException {
        // Given
        ResourceNotFoundException exception = new ResourceNotFoundException("Resource not found");
        Method method = this.getClass().getMethod("testMethod");
        Object[] params = {"param1"};

        // When & Then - Should handle ResourceNotFoundException specifically
        assertDoesNotThrow(() -> {
            asyncExceptionHandler.handleUncaughtException(exception, method, params);
        });
    }

    // Helper method for testing
    public void testMethod() {
        // Empty method for testing reflection
    }
}