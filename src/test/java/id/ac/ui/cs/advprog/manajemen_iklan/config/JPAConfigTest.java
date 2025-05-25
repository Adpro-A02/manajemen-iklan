package id.ac.ui.cs.advprog.manajemen_iklan.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.AuditorAware;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import jakarta.persistence.EntityManagerFactory;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JPAConfigTest {

    @Mock
    private EntityManagerFactory entityManagerFactory;

    private JPAConfig jpaConfig;

    @BeforeEach
    void setUp() {
        jpaConfig = new JPAConfig();
    }

    @Test
    void testAuditorProvider() {
        // When
        AuditorAware<String> auditorProvider = jpaConfig.auditorProvider();

        // Then
        assertNotNull(auditorProvider);
        
        Optional<String> currentAuditor = auditorProvider.getCurrentAuditor();
        assertTrue(currentAuditor.isPresent());
        assertEquals("system", currentAuditor.get());
    }

    @Test
    void testTransactionManager() {
        // When
        PlatformTransactionManager transactionManager = jpaConfig.transactionManager(entityManagerFactory);

        // Then
        assertNotNull(transactionManager);
        assertTrue(transactionManager instanceof JpaTransactionManager);
        
        JpaTransactionManager jpaTransactionManager = (JpaTransactionManager) transactionManager;
        assertEquals(entityManagerFactory, jpaTransactionManager.getEntityManagerFactory());
    }

    @Test
    void testConfigurationAnnotations() {
        // Verify that the class has the necessary annotations
        assertTrue(jpaConfig.getClass().isAnnotationPresent(org.springframework.context.annotation.Configuration.class));
        assertTrue(jpaConfig.getClass().isAnnotationPresent(org.springframework.data.jpa.repository.config.EnableJpaAuditing.class));
        assertTrue(jpaConfig.getClass().isAnnotationPresent(org.springframework.transaction.annotation.EnableTransactionManagement.class));
        assertTrue(jpaConfig.getClass().isAnnotationPresent(org.springframework.cache.annotation.EnableCaching.class));
        assertTrue(jpaConfig.getClass().isAnnotationPresent(org.springframework.data.jpa.repository.config.EnableJpaRepositories.class));
    }

    @Test
    void testJpaRepositoriesConfiguration() {
        // Get the EnableJpaRepositories annotation
        org.springframework.data.jpa.repository.config.EnableJpaRepositories annotation = 
            jpaConfig.getClass().getAnnotation(org.springframework.data.jpa.repository.config.EnableJpaRepositories.class);

        // Verify configuration
        assertNotNull(annotation);
        assertEquals(1, annotation.basePackages().length);
        assertEquals("id.ac.ui.cs.advprog.manajemen_iklan.repository", annotation.basePackages()[0]);
        assertEquals("entityManagerFactory", annotation.entityManagerFactoryRef());
        assertEquals("transactionManager", annotation.transactionManagerRef());
    }
}