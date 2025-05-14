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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
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
        Pageable pageable = createPageRequest(pageNumber, pageSize);
        
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
        
        return createListResponse(
                advertisementsPage, 
                pageNumber + 1, 
                pageSize, 
                "Daftar iklan berhasil diambil"
        );
    }
    
    @Override
    public IklanResponseDTO getAdvertisementById(String id) {
        logger.debug("Fetching advertisement with id: {}", id);
        
        IklanModel iklan = findIklanById(id);
        
        return createDetailResponse(
                mapToDTO(iklan),
                HttpStatus.OK.value(),
                "Iklan berhasil diambil"
        );
    }
    
    @Override
    @Transactional
    @CacheEvict(value = "advertisementsCache", allEntries = true)
    public IklanResponseDTO createAdvertisement(IklanDTO iklanDTO) {
        logger.debug("Creating new advertisement: {}", iklanDTO.getTitle());
        
        IklanModel iklan = createIklanModelFromDTO(iklanDTO);
        IklanModel savedIklan = iklanRepository.save(iklan);
        
        return createDetailResponse(
                mapToDTO(savedIklan),
                HttpStatus.CREATED.value(),
                "Iklan berhasil dibuat"
        );
    }
    
    @Override
    @Transactional
    @CacheEvict(value = "advertisementsCache", allEntries = true)
    public IklanResponseDTO updateAdvertisement(String id, IklanDTO iklanDTO) {
        logger.debug("Updating advertisement with id: {}", id);
        
        IklanModel existingIklan = findIklanById(id);
        updateIklanFromDTO(existingIklan, iklanDTO);
        
        IklanModel updatedIklan = iklanRepository.save(existingIklan);
        
        return createDetailResponse(
                mapToDTO(updatedIklan),
                HttpStatus.OK.value(),
                "Iklan berhasil diperbarui"
        );
    }
    
    @Override
    @Transactional
    @CacheEvict(value = "advertisementsCache", allEntries = true)
    public IklanResponseDTO updateAdvertisementStatus(String id, IklanStatus status) {
        logger.debug("Updating status of advertisement with id: {} to {}", id, status);
        
        if (status == null) {
            throw new InvalidStatusException("Status tidak boleh kosong");
        }
        
        IklanModel existingIklan = findIklanById(id);
        existingIklan.setStatus(status);
        
        IklanModel updatedIklan = iklanRepository.save(existingIklan);
        
        return createDetailResponse(
                mapToDTO(updatedIklan),
                HttpStatus.OK.value(),
                "Status iklan berhasil diperbarui"
        );
    }
    
    @Override
    @Transactional
    @CacheEvict(value = "advertisementsCache", allEntries = true)
    public IklanResponseDTO deleteAdvertisement(String id) {
        logger.debug("Deleting advertisement with id: {}", id);
        
        IklanModel existingIklan = findIklanById(id);
        iklanRepository.delete(existingIklan);
        
        return IklanResponseDTO.builder()
                .code(HttpStatus.OK.value())
                .success(true)
                .message("Iklan berhasil dihapus")
                .build();
    }
    
    // Helper methods
    private Pageable createPageRequest(int pageNumber, int pageSize) {
        return PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
    }
    
    private IklanModel findIklanById(String id) {
        return iklanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Iklan dengan id " + id + " tidak ditemukan"));
    }
    
    private IklanModel createIklanModelFromDTO(IklanDTO dto) {
        return IklanModel.builder()
                .title(dto.getTitle())
                .imageUrl(dto.getImageUrl())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .status(dto.getStatus() != null ? dto.getStatus() : IklanStatus.INACTIVE)
                .clickUrl(dto.getClickUrl())
                .build();
    }
    
    private void updateIklanFromDTO(IklanModel iklan, IklanDTO dto) {
        iklan.setTitle(dto.getTitle());
        iklan.setImageUrl(dto.getImageUrl());
        iklan.setStartDate(dto.getStartDate());
        iklan.setEndDate(dto.getEndDate());
        iklan.setStatus(dto.getStatus());
        iklan.setClickUrl(dto.getClickUrl());
    }
    
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
    
    private IklanResponseDTO createListResponse(Page<IklanModel> page, int currentPage, int limit, String message) {
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
                .code(HttpStatus.OK.value())
                .success(true)
                .message(message)
                .data(dataDTO)
                .build();
    }
    
    private IklanResponseDTO createDetailResponse(IklanDTO dto, int statusCode, String message) {
        return IklanResponseDTO.builder()
                .code(statusCode)
                .success(true)
                .message(message)
                .data(IklanResponseDTO.IklanDataDTO.builder()
                        .advertisement(dto)
                        .build())
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