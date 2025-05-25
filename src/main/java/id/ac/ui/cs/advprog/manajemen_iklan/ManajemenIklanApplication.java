package id.ac.ui.cs.advprog.manajemen_iklan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableScheduling
public class ManajemenIklanApplication {

    public static void main(String[] args) {
        SpringApplication.run(ManajemenIklanApplication.class, args);
    }
}