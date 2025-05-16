package id.ac.ui.cs.advprog.manajemen_iklan.controller;

import id.ac.ui.cs.advprog.manajemen_iklan.dto.AuthRequestDTO;
import id.ac.ui.cs.advprog.manajemen_iklan.dto.IklanResponseDTO;
import id.ac.ui.cs.advprog.manajemen_iklan.dto.RegisterRequestDTO;
import id.ac.ui.cs.advprog.manajemen_iklan.model.UserModel;
import id.ac.ui.cs.advprog.manajemen_iklan.service.UserService;
import id.ac.ui.cs.advprog.manajemen_iklan.utils.JwtTokenUtil;
import id.ac.ui.cs.advprog.manajemen_iklan.utils.ResponseFactory;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenUtil jwtTokenUtil;
    private final ResponseFactory responseFactory;

    public AuthController(
            AuthenticationManager authenticationManager, 
            UserService userService, 
            JwtTokenUtil jwtTokenUtil,
            ResponseFactory responseFactory) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.responseFactory = responseFactory;
    }

    @PostMapping("/register")
    public ResponseEntity<IklanResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        try {
            if (userService.usernameExists(request.getUsername())) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(responseFactory.createErrorResponse(
                                HttpStatus.CONFLICT.value(), 
                                "Username sudah terdaftar"
                        ));
            }
            
            // Create the user
            UserModel user = userService.registerNewUser(
                    request.getUsername(),
                    request.getPassword(),
                    request.getName(),
                    request.getRoles()
            );
            
            // Generate token
            String token = jwtTokenUtil.generateToken(user);
            
            // Prepare response data
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("username", user.getUsername());
            data.put("name", user.getName());
            data.put("roles", user.getRoles());
            
            // Create response
            IklanResponseDTO response = responseFactory.createCustomResponse(
                    data,
                    HttpStatus.CREATED.value(),
                    "Pendaftaran berhasil"
            );
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            logger.error("Error during registration", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(responseFactory.createErrorResponse(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Terjadi kesalahan saat melakukan pendaftaran: " + e.getMessage()
                    ));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<IklanResponseDTO> authenticate(@Valid @RequestBody AuthRequestDTO request) {
        try {
            // Authenticate the user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
            
            // Get the authenticated user
            UserModel user = (UserModel) authentication.getPrincipal();
            
            // Generate token
            String token = jwtTokenUtil.generateToken(user);
            
            // Prepare response data
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("username", user.getUsername());
            data.put("name", user.getName());
            data.put("roles", user.getRoles());
            
            // Create response
            IklanResponseDTO response = responseFactory.createCustomResponse(
                    data,
                    HttpStatus.OK.value(),
                    "Login berhasil"
            );
            
            return ResponseEntity.ok(response);
            
        } catch (BadCredentialsException e) {
            logger.warn("Failed login attempt for username: {}", request.getUsername());
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(responseFactory.createErrorResponse(
                            HttpStatus.UNAUTHORIZED.value(),
                            "Username atau password salah"
                    ));
        } catch (Exception e) {
            logger.error("Error during login", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(responseFactory.createErrorResponse(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Terjadi kesalahan saat melakukan login"
                    ));
        }
    }
}