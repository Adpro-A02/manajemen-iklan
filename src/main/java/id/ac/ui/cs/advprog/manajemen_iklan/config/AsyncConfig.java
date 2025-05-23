package id.ac.ui.cs.advprog.manajemen_iklan.config;

import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {
    
    private static final Logger logger = LoggerFactory.getLogger(AsyncConfig.class);
    
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);            
        executor.setMaxPoolSize(10);           
        executor.setQueueCapacity(25);        
        executor.setThreadNamePrefix("iklan-async-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        
        // This is critical for propagating the security context
        executor.setTaskDecorator(runnable -> {
            var context = SecurityContextHolder.getContext();
            return () -> {
                try {
                    SecurityContextHolder.setContext(context);
                    runnable.run();
                } finally {
                    SecurityContextHolder.clearContext();
                }
            };
        });
        
        executor.initialize();
        return executor;
    }
    
    @Bean(name = "trackingExecutor")
    public Executor trackingExecutor() {
        // Dedicated executor for high-volume tracking operations
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(100); // Higher queue capacity for tracking
        executor.setThreadNamePrefix("tracking-");
        executor.setRejectedExecutionHandler((r, e) -> logger.warn("Tracking task rejected"));
        executor.initialize();
        return executor;
    }
    
    @Override
    public Executor getAsyncExecutor() {
        return taskExecutor();
    }
    
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) -> {
            logger.error(
                "Async error in method '{}' with parameters {}: {}",
                method.getName(),
                params,
                ex.getMessage(),
                ex
            );
        };
    }
}