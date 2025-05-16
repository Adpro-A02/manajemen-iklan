package id.ac.ui.cs.advprog.manajemen_iklan.config;

import id.ac.ui.cs.advprog.manajemen_iklan.model.UserModel;
import id.ac.ui.cs.advprog.manajemen_iklan.repository.UserRepository;
import id.ac.ui.cs.advprog.manajemen_iklan.service.UserService;
import id.ac.ui.cs.advprog.manajemen_iklan.utils.JwtTokenUtil;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@TestConfiguration
public class TestConfig {
    
    @Bean
    @Primary
    public UserDetailsService userDetailsService() {
        UserDetails admin = User.withUsername("admin")
                .password("$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW") // admin123
                .roles("ADMIN")
                .build();
                
        UserDetails user = User.withUsername("user")
                .password("$2a$10$9tWe/MDXo.xoyFylWr9lBuJd2nQjrYLjuRuGy4bgd6HRbvS/vLS3K") // user123
                .roles("USER")
                .build();
                
        return new InMemoryUserDetailsManager(admin, user);
    }
    
    @Bean
    @Primary
    public UserRepository userRepository() {
        UserRepository mockRepo = Mockito.mock(UserRepository.class);
        
        // Create a test UserModel
        UserModel testAdmin = UserModel.builder()
                .id("1")
                .username("admin")
                .password("$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW")
                .name("Admin User")
                .roles(Arrays.asList("ADMIN"))
                .build();
                
        // Set up mock behavior
        when(mockRepo.findByUsername("admin")).thenReturn(Optional.of(testAdmin));
        when(mockRepo.save(any(UserModel.class))).thenAnswer(i -> i.getArgument(0));
        
        return mockRepo;
    }
    
    @Bean
    @Primary
    @Lazy 
    public UserService userService() {
        return new UserService(userRepository(), passwordEncoder());
    }
    
    @Bean
    @Primary
    public JwtTokenUtil jwtTokenUtil() {
        return Mockito.mock(JwtTokenUtil.class);
    }
    
    @Bean
    @Primary  
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    @Primary
    public AuthenticationManager authenticationManager() {
        return Mockito.mock(AuthenticationManager.class);
    }
}