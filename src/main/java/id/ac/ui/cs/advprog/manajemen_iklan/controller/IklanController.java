package id.ac.ui.cs.advprog.manajemen_iklan.controller;

import id.ac.ui.cs.advprog.manajemen_iklan.dto.IklanDTO;
import id.ac.ui.cs.advprog.manajemen_iklan.dto.IklanResponseDTO;
import id.ac.ui.cs.advprog.manajemen_iklan.enums.IklanStatus;
import id.ac.ui.cs.advprog.manajemen_iklan.service.IklanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/advertisements")
@RequiredArgsConstructor
public class IklanController {
    
    private static final Logger logger = LoggerFactory.getLogger(IklanController.class);
    private final IklanService iklanService;
    
    // get all ads
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
    
    // create
    @PostMapping
    public ResponseEntity<IklanResponseDTO> createAdvertisement(@Valid @RequestBody IklanDTO iklanDTO) {
        logger.info("Creating new advertisement: {}", iklanDTO.getTitle());
        
        IklanResponseDTO response = iklanService.createAdvertisement(iklanDTO);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    // get by id
    @GetMapping("/{id}")
    public ResponseEntity<IklanResponseDTO> getAdvertisementById(@PathVariable String id) {
        logger.info("Getting advertisement with id: {}", id);
        
        IklanResponseDTO response = iklanService.getAdvertisementById(id);
        
        return ResponseEntity.ok(response);
    }
    
    // update ad
    @PutMapping("/{id}")
    public ResponseEntity<IklanResponseDTO> updateAdvertisement(
            @PathVariable String id, 
            @Valid @RequestBody IklanDTO iklanDTO) {
        logger.info("Updating advertisement with id: {}", id);
        
        IklanResponseDTO response = iklanService.updateAdvertisement(id, iklanDTO);
        
        return ResponseEntity.ok(response);
    }
    
    // delete ad
    @DeleteMapping("/{id}")
    public ResponseEntity<IklanResponseDTO> deleteAdvertisement(@PathVariable String id) {
        logger.info("Deleting advertisement with id: {}", id);
        
        IklanResponseDTO response = iklanService.deleteAdvertisement(id);
        
        return ResponseEntity.ok(response);
    }

    // update status
    @PatchMapping("/{id}/status")
    public ResponseEntity<IklanResponseDTO> updateStatus(
            @PathVariable String id,
            @Valid @RequestBody IklanDTO statusUpdate) {
        
        logger.info("Updating status of advertisement with id: {} to {}", id, statusUpdate.getStatus());
        
        IklanStatus status = statusUpdate.getStatus();

        IklanResponseDTO response = iklanService.updateAdvertisementStatus(id, status);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/batch-status")
    public ResponseEntity<Void> batchUpdateStatus(
            @Valid @RequestBody Map<String, Object> request) {
        
        @SuppressWarnings("unchecked")
        List<String> ids = (List<String>) request.get("ids");
        IklanStatus status = IklanStatus.valueOf((String) request.get("status"));
        
        // Start async processing and return immediately
        iklanService.batchUpdateStatusAsync(ids, status);
        
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/reports/generate")
    public ResponseEntity<Void> generateReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        // Start async report generation and return immediately
        iklanService.generateAdvertisementReportAsync(startDate, endDate);
        
        return ResponseEntity.accepted().build();
    }
}