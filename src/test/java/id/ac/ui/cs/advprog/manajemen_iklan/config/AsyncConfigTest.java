package id.ac.ui.cs.advprog.manajemen_iklan.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.*;

class AsyncConfigTest {

    private AsyncConfig asyncConfig;

    @BeforeEach
    void setUp() {
        asyncConfig = new AsyncConfig();
    }

    @Test
    void testTaskExecutorConfiguration() {
        // When
        Executor executor = asyncConfig.taskExecutor();

        // Then
        assertNotNull(executor);
        assertTrue(executor instanceof ThreadPoolTaskExecutor);
        
        ThreadPoolTaskExecutor taskExecutor = (ThreadPoolTaskExecutor) executor;
        assertEquals(5, taskExecutor.getCorePoolSize());
        assertEquals(10, taskExecutor.getMaxPoolSize());
        assertEquals(25, taskExecutor.getQueueCapacity());
        assertTrue(taskExecutor.getThreadNamePrefix().startsWith("iklan-async-"));
        
        // Initialize to access properties
        taskExecutor.initialize();
        assertNotNull(taskExecutor.getThreadPoolExecutor());
    }

    @Test
    void testTrackingExecutorConfiguration() {
        // When
        Executor executor = asyncConfig.trackingExecutor();

        // Then
        assertNotNull(executor);
        assertTrue(executor instanceof ThreadPoolTaskExecutor);
        
        ThreadPoolTaskExecutor taskExecutor = (ThreadPoolTaskExecutor) executor;
        assertEquals(3, taskExecutor.getCorePoolSize());
        assertEquals(5, taskExecutor.getMaxPoolSize());
        assertEquals(100, taskExecutor.getQueueCapacity());
        assertTrue(taskExecutor.getThreadNamePrefix().startsWith("tracking-"));
        
        // Initialize to access properties
        taskExecutor.initialize();
        assertNotNull(taskExecutor.getThreadPoolExecutor());
    }

    @Test
    void testGetAsyncExecutor() {
        // When
        Executor executor = asyncConfig.getAsyncExecutor();

        // Then
        assertNotNull(executor);
        assertTrue(executor instanceof ThreadPoolTaskExecutor);
    }

    @Test
    void testGetAsyncUncaughtExceptionHandler() {
        // When
        AsyncUncaughtExceptionHandler handler = asyncConfig.getAsyncUncaughtExceptionHandler();

        // Then
        assertNotNull(handler);
    }

    @Test
    void testTaskExecutorInitialization() {
        // When
        ThreadPoolTaskExecutor executor = (ThreadPoolTaskExecutor) asyncConfig.taskExecutor();
        executor.initialize();

        // Then
        assertNotNull(executor.getThreadPoolExecutor());
        assertTrue(executor.getThreadPoolExecutor().getCorePoolSize() >= 0);
    }

    @Test
    void testTrackingExecutorInitialization() {
        // When
        ThreadPoolTaskExecutor executor = (ThreadPoolTaskExecutor) asyncConfig.trackingExecutor();
        executor.initialize();

        // Then
        assertNotNull(executor.getThreadPoolExecutor());
        assertTrue(executor.getThreadPoolExecutor().getCorePoolSize() >= 0);
    }

    @Test
    void testAsyncConfigImplementsAsyncConfigurer() {
        // Then
        assertTrue(asyncConfig instanceof org.springframework.scheduling.annotation.AsyncConfigurer);
    }

    @Test
    void testDifferentExecutorInstances() {
        // When
        Executor taskExecutor = asyncConfig.taskExecutor();
        Executor trackingExecutor = asyncConfig.trackingExecutor();
        Executor asyncExecutor = asyncConfig.getAsyncExecutor();

        // Then
        assertNotSame(taskExecutor, trackingExecutor);
        assertNotNull(taskExecutor);
        assertNotNull(trackingExecutor);
        assertNotNull(asyncExecutor);
    }
}