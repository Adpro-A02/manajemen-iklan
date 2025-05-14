package id.ac.ui.cs.advprog.manajemen_iklan.service;

import id.ac.ui.cs.advprog.manajemen_iklan.dto.IklanDTO;
import id.ac.ui.cs.advprog.manajemen_iklan.dto.IklanResponseDTO;
import id.ac.ui.cs.advprog.manajemen_iklan.enums.IklanStatus;
import id.ac.ui.cs.advprog.manajemen_iklan.exception.InvalidStatusException;
import id.ac.ui.cs.advprog.manajemen_iklan.exception.ResourceNotFoundException;
import id.ac.ui.cs.advprog.manajemen_iklan.model.IklanModel;
import id.ac.ui.cs.advprog.manajemen_iklan.repository.IklanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class IklanServiceImpl implements IklanService {
    private static final Logger logger = LoggerFactory.getLogger(IklanServiceImpl.class);
    private final IklanRepository iklanRepository;
    
    @Override
    @Cacheable(value = "advertisementsCache", 
               key = "{#page, #limit, #statusParam, #startDateFrom, #startDateTo, #endDateFrom, #endDateTo, #search}",
               condition = "#search == null || #search.length() > 3")
    public IklanResponseDTO getAllAdvertisements(
            Integer page,
            Integer limit,
            String statusParam,
            LocalDateTime startDateFrom,
            LocalDateTime startDateTo,
            LocalDateTime endDateFrom,
            LocalDateTime endDateTo,
            String search) {
        
        logger.debug("Fetching advertisements with filters: status={}, search={}, page={}, limit={}", 
                statusParam, search, page, limit);
        
        // Set default values if null
        int pageNumber = (page == null || page < 1) ? 0 : page - 1;
        int pageSize = (limit == null || limit < 1) ? 10 : Math.min(limit, 50);
        
        // Parse status if provided
        IklanStatus status = parseStatus(statusParam);
        
        // Create pagination request
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        // Fetch advertisements with filters
        Page<IklanModel> advertisementsPage = iklanRepository.findAllWithFilters(
                status,
                startDateFrom,
                startDateTo,
                endDateFrom,
                endDateTo,
                search,
                pageable);
        
        logger.debug("Found {} advertisements matching the criteria", advertisementsPage.getTotalElements());
        
        return createSuccessResponse(
                advertisementsPage, 
                pageNumber + 1, 
                pageSize, 
                "Daftar iklan berhasil diambil"
        );
    }
    
    @Override
    public IklanResponseDTO getAdvertisementById(String id) {
        logger.debug("Fetching advertisement with id: {}", id);
        
        IklanModel iklan = iklanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Iklan dengan id " + id + " tidak ditemukan"));
        
        IklanDTO iklanDTO = mapToDTO(iklan);
        
        return IklanResponseDTO.builder()
                .code(200)
                .success(true)
                .message("Iklan berhasil diambil")
                .data(IklanResponseDTO.IklanDataDTO.builder()
                        .advertisement(iklanDTO)
                        .build())
                .build();
    }
    
    @Override
    @Transactional
    @CacheEvict(value = "advertisementsCache", allEntries = true)
    public IklanResponseDTO createAdvertisement(IklanDTO iklanDTO) {
        logger.debug("Creating new advertisement: {}", iklanDTO.getTitle());
        
        IklanModel iklan = IklanModel.builder()
                .title(iklanDTO.getTitle())
                .imageUrl(iklanDTO.getImageUrl())
                .startDate(iklanDTO.getStartDate())
                .endDate(iklanDTO.getEndDate())
                .status(iklanDTO.getStatus() != null ? iklanDTO.getStatus() : IklanStatus.INACTIVE)
                .clickUrl(iklanDTO.getClickUrl())
                .build();
        
        IklanModel savedIklan = iklanRepository.save(iklan);
        
        return IklanResponseDTO.builder()
                .code(201)
                .success(true)
                .message("Iklan berhasil dibuat")
                .data(IklanResponseDTO.IklanDataDTO.builder()
                        .advertisement(mapToDTO(savedIklan))
                        .build())
                .build();
    }
    
    @Override
    @Transactional
    @CacheEvict(value = "advertisementsCache", allEntries = true)
    public IklanResponseDTO updateAdvertisement(String id, IklanDTO iklanDTO) {
        logger.debug("Updating advertisement with id: {}", id);
        
        IklanModel existingIklan = iklanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Iklan dengan id " + id + " tidak ditemukan"));
        
        // Update fields
        existingIklan.setTitle(iklanDTO.getTitle());
        existingIklan.setImageUrl(iklanDTO.getImageUrl());
        existingIklan.setStartDate(iklanDTO.getStartDate());
        existingIklan.setEndDate(iklanDTO.getEndDate());
        existingIklan.setStatus(iklanDTO.getStatus());
        existingIklan.setClickUrl(iklanDTO.getClickUrl());
        
        IklanModel updatedIklan = iklanRepository.save(existingIklan);
        
        return IklanResponseDTO.builder()
                .code(200)
                .success(true)
                .message("Iklan berhasil diperbarui")
                .data(IklanResponseDTO.IklanDataDTO.builder()
                        .advertisement(mapToDTO(updatedIklan))
                        .build())
                .build();
    }
    
    @Override
    @Transactional
    @CacheEvict(value = "advertisementsCache", allEntries = true)
    public IklanResponseDTO updateAdvertisementStatus(String id, IklanStatus status) {
        logger.debug("Updating status of advertisement with id: {} to {}", id, status);
        
        IklanModel existingIklan = iklanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Iklan dengan id " + id + " tidak ditemukan"));
        
        existingIklan.setStatus(status);
        
        IklanModel updatedIklan = iklanRepository.save(existingIklan);
        
        return IklanResponseDTO.builder()
                .code(200)
                .success(true)
                .message("Status iklan berhasil diperbarui")
                .data(IklanResponseDTO.IklanDataDTO.builder()
                        .advertisement(mapToDTO(updatedIklan))
                        .build())
                .build();
    }
    
    @Override
    @Transactional
    @CacheEvict(value = "advertisementsCache", allEntries = true)
    public IklanResponseDTO deleteAdvertisement(String id) {
        logger.debug("Deleting advertisement with id: {}", id);
        
        IklanModel existingIklan = iklanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Iklan dengan id " + id + " tidak ditemukan"));
        
        iklanRepository.delete(existingIklan);
        
        return IklanResponseDTO.builder()
                .code(200)
                .success(true)
                .message("Iklan berhasil dihapus")
                .build();
    }
    
    // Helper methods
    private IklanStatus parseStatus(String statusParam) {
        if (statusParam == null || statusParam.isEmpty()) {
            return null;
        }
        
        try {
            switch(statusParam.toLowerCase()) {
            case "active":
                return IklanStatus.ACTIVE;
            case "inactive":
                return IklanStatus.INACTIVE;
            case "expired":
                return IklanStatus.EXPIRED;
            default:
                logger.warn("Invalid status value provided: {}", statusParam);
                return null;
            }
        } catch (IllegalArgumentException e) {
            logger.error("Error parsing status value: {}", statusParam, e);
            return null;
        }
    }
    
    private IklanResponseDTO createSuccessResponse(Page<IklanModel> page, int currentPage, int limit, String message) {
        // Map entities to DTOs
        List<IklanDTO> advertisementDTOs = page.getContent().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        
        // Create pagination info
        IklanResponseDTO.PaginationDTO paginationDTO = IklanResponseDTO.PaginationDTO.builder()
                .currentPage(currentPage)
                .totalPages(page.getTotalPages())
                .totalItems(page.getTotalElements())
                .limit(limit)
                .build();
        
        // Create data object
        IklanResponseDTO.IklanDataDTO dataDTO = IklanResponseDTO.IklanDataDTO.builder()
                .advertisements(advertisementDTOs)
                .pagination(paginationDTO)
                .build();
        
        // Create response
        return IklanResponseDTO.builder()
                .code(200)
                .success(true)
                .message(message)
                .data(dataDTO)
                .build();
    }
    
    private IklanDTO mapToDTO(IklanModel model) {
        return IklanDTO.builder()
                .id(model.getId())
                .title(model.getTitle())
                .imageUrl(model.getImageUrl())
                .startDate(model.getStartDate())
                .endDate(model.getEndDate())
                .status(model.getStatus())
                .clickUrl(model.getClickUrl())
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .build();
    }
}