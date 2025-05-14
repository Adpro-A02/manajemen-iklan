package id.ac.ui.cs.advprog.manajemen_iklan.controller;

import id.ac.ui.cs.advprog.manajemen_iklan.dto.IklanResponseDTO;
import id.ac.ui.cs.advprog.manajemen_iklan.service.IklanService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for public-facing advertisement endpoints
 */
@RestController
@RequestMapping("/api/v1/public/advertisements")
@RequiredArgsConstructor
public class PublicIklanController {
    
    private static final Logger logger = LoggerFactory.getLogger(PublicIklanController.class);
    private final IklanService iklanService;

    //Get active advertisements for public display
    @GetMapping
    public ResponseEntity<IklanResponseDTO> getPublicAdvertisements(
            @RequestParam(required = false) String position,
            @RequestParam(required = false, defaultValue = "3") Integer limit) {
        
        logger.info("Getting public advertisements with position: {}, limit: {}", position, limit);
        
        IklanResponseDTO response = iklanService.getPublicAdvertisements(position, limit);
        
        return ResponseEntity.ok(response);
    }
}