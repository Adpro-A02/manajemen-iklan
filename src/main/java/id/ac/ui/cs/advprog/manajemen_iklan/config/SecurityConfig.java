package id.ac.ui.cs.advprog.manajemen_iklan.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authorize -> authorize
                // Define which endpoints need to be authenticated
                // For now, we'll require authentication for all advertisement endpoints
                .requestMatchers("/api/v1/advertisements/**").authenticated()
                // You can add more specific rules as needed
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(STATELESS));
            
        // Additional security configuration will go here
        // Such as JWT token filter, OAuth2 setup, etc.
        
        return http.build();
    }
    
    // Add more beans as needed (e.g., AuthenticationProvider, PasswordEncoder, etc.)
}