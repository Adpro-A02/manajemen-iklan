package id.ac.ui.cs.advprog.manajemen_iklan.controller;

import id.ac.ui.cs.advprog.manajemen_iklan.dto.ErrorResponseDTO;
import id.ac.ui.cs.advprog.manajemen_iklan.dto.IklanDTO;
import id.ac.ui.cs.advprog.manajemen_iklan.dto.IklanResponseDTO;
import id.ac.ui.cs.advprog.manajemen_iklan.exception.InvalidStatusException;
import id.ac.ui.cs.advprog.manajemen_iklan.service.IklanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/advertisements")
@RequiredArgsConstructor
public class IklanController {
    
    private static final Logger logger = LoggerFactory.getLogger(IklanController.class);
    private final IklanService iklanService;
    
    @GetMapping
    public ResponseEntity<IklanResponseDTO> getAllAdvertisements(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDateTo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDateTo,
            @RequestParam(required = false) String search) {
        
        logger.info("Getting all advertisements with filters: status={}, search={}, page={}, limit={}", 
                status, search, page, limit);
        
        IklanResponseDTO response = iklanService.getAllAdvertisements(
                page, limit, status, startDateFrom, startDateTo, endDateFrom, endDateTo, search);
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping
    public ResponseEntity<IklanResponseDTO> createAdvertisement(@Valid @RequestBody IklanDTO iklanDTO) {
        logger.info("Creating new advertisement: {}", iklanDTO.getTitle());
        
        // Call service to create advertisement (you'll need to implement this)
        // IklanResponseDTO response = iklanService.createAdvertisement(iklanDTO);
        
        // Placeholder response
        IklanResponseDTO response = IklanResponseDTO.builder()
                .code(HttpStatus.CREATED.value())
                .success(true)
                .message("Iklan berhasil dibuat")
                .build();
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<IklanResponseDTO> getAdvertisementById(@PathVariable String id) {
        logger.info("Getting advertisement with id: {}", id);
        
        // Call service to get advertisement by id (you'll need to implement this)
        // IklanResponseDTO response = iklanService.getAdvertisementById(id);
        
        // Placeholder response
        IklanResponseDTO response = IklanResponseDTO.builder()
                .code(HttpStatus.OK.value())
                .success(true)
                .message("Iklan berhasil diambil")
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<IklanResponseDTO> updateAdvertisement(
            @PathVariable String id, 
            @Valid @RequestBody IklanDTO iklanDTO) {
        logger.info("Updating advertisement with id: {}", id);
        
        // Call service to update advertisement (you'll need to implement this)
        // IklanResponseDTO response = iklanService.updateAdvertisement(id, iklanDTO);
        
        // Placeholder response
        IklanResponseDTO response = IklanResponseDTO.builder()
                .code(HttpStatus.OK.value())
                .success(true)
                .message("Iklan berhasil diperbarui")
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<IklanResponseDTO> deleteAdvertisement(@PathVariable String id) {
        logger.info("Deleting advertisement with id: {}", id);
        
        // Call service to delete advertisement (you'll need to implement this)
        // IklanResponseDTO response = iklanService.deleteAdvertisement(id);
        
        // Placeholder response
        IklanResponseDTO response = IklanResponseDTO.builder()
                .code(HttpStatus.OK.value())
                .success(true)
                .message("Iklan berhasil dihapus")
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleException(Exception ex) {
        logger.error("Unhandled exception occurred", ex);
        
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .success(false)
                .message("Terjadi kesalahan pada server")
                .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponseDTO> handleRuntimeException(RuntimeException ex) {
        logger.error("Runtime exception occurred", ex);
        
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .success(false)
                .message(ex.getMessage())
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDTO> handleAccessDeniedException(AccessDeniedException ex) {
        logger.warn("Access denied: {}", ex.getMessage());
        
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .code(HttpStatus.FORBIDDEN.value())
                .success(false)
                .message("Anda tidak memiliki akses untuk melakukan operasi ini")
                .build();
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationExceptions(MethodArgumentNotValidException ex) {
        logger.warn("Validation error: {}", ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );
        
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .success(false)
                .message("Validasi gagal")
                .errors(errors)
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    @ExceptionHandler(InvalidStatusException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidStatusException(InvalidStatusException ex) {
        logger.warn("Invalid status: {}", ex.getMessage());
        
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .success(false)
                .message(ex.getMessage())
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}