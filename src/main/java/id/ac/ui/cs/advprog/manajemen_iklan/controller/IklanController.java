package id.ac.ui.cs.advprog.manajemen_iklan.controller;

import id.ac.ui.cs.advprog.manajemen_iklan.dto.ErrorResponseDTO;
import id.ac.ui.cs.advprog.manajemen_iklan.dto.IklanResponseDTO;
import id.ac.ui.cs.advprog.manajemen_iklan.service.IklanService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/advertisements")
@RequiredArgsConstructor
public class IklanController {
    
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
        
        IklanResponseDTO response = iklanService.getAllAdvertisements(
                page, limit, status, startDateFrom, startDateTo, endDateFrom, endDateTo, search);
        
        return ResponseEntity.ok(response);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleException(Exception ex) {
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .success(false)
                .message("Terjadi kesalahan pada server")
                .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
    
    // Add more exception handlers for specific exceptions
    // For example:
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponseDTO> handleRuntimeException(RuntimeException ex) {
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .success(false)
                .message(ex.getMessage())
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}