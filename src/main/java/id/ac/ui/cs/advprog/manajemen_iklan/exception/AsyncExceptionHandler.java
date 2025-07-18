package id.ac.ui.cs.advprog.manajemen_iklan.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

@Component
public class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(AsyncExceptionHandler.class);

    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {
        String methodName = method != null ? method.getName() : "unknown";
        String paramString = params != null ? Arrays.toString(params) : "[]";
        String exceptionMessage = ex != null ? ex.getMessage() : "null";
        
        logger.error(
            "Async method '{}' failed with parameters {} and exception: {}",
            methodName,
            paramString,
            exceptionMessage,
            ex
        );
        
        // Additional error handling like notifications or retries could be added here
        if (ex instanceof ResourceNotFoundException) {
            logger.warn("Resource not found in async operation: {}", exceptionMessage);
        } else {
            logger.error("Critical error in async operation", ex);
        }
    }
}