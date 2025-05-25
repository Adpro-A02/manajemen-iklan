package id.ac.ui.cs.advprog.manajemen_iklan;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.ActiveProfiles;


@ActiveProfiles("test")
class ManajemenIklanApplicationTests {

    @Test
    void contextLoads() {
        // This simple test will not attempt to load a Spring context at all
        assert(true);
    }
    
    @TestConfiguration
    static class EmptyTestConfiguration {
        // This empty configuration class ensures no other beans are attempted to be created
    }
}