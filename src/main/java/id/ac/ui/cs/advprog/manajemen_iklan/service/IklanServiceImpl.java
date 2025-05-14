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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        
        validateId(id);
        IklanModel iklan = findIklanById(id);
        
        return createDetailResponse(
        mapToDTO(iklan),
        HttpStatus.OK.value(),
        "Detail iklan berhasil diambil"  // Updated message to match documentation
        );
    }
    
    @Override
    @Transactional
    @CacheEvict(value = "advertisementsCache", allEntries = true)
    public IklanResponseDTO createAdvertisement(IklanDTO iklanDTO) {
        logger.debug("Creating new advertisement: {}", iklanDTO.getTitle());
        
        // Validate DTO
        validateAdvertisementDTO(iklanDTO);
        
        // Apply sanitization if the method exists
        if (iklanDTO instanceof IklanDTO) {
            sanitizeDTO(iklanDTO);
        }
        
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
        
        // Validate inputs
        validateId(id);
        validateAdvertisementDTO(iklanDTO);
        
        // Apply sanitization if the method exists
        if (iklanDTO instanceof IklanDTO) {
            sanitizeDTO(iklanDTO);
        }
        
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
        
        // Validate inputs
        validateId(id);
        validateStatus(status);
        
        IklanModel existingIklan = findIklanById(id);
        existingIklan.setStatus(status);
        
        IklanModel updatedIklan = iklanRepository.save(existingIklan);
        
        // Create simplified response with only required fields
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("id", updatedIklan.getId());
        responseData.put("status", updatedIklan.getStatus().toString().toLowerCase());
        responseData.put("updatedAt", updatedIklan.getUpdatedAt());
        
        return IklanResponseDTO.builder()
            .code(HttpStatus.OK.value())
            .success(true)
            .message("Status iklan berhasil diperbarui")
            .data(responseData)
            .build();
    }
    
    @Override
    @Transactional
    @CacheEvict(value = "advertisementsCache", allEntries = true)
    public IklanResponseDTO deleteAdvertisement(String id) {
        logger.debug("Deleting advertisement with id: {}", id);
        
        validateId(id);
        IklanModel existingIklan = findIklanById(id);
        iklanRepository.delete(existingIklan);
        
        return IklanResponseDTO.builder()
                .code(HttpStatus.OK.value())
                .success(true)
                .message("Iklan berhasil dihapus")
                .build();
    }
    
    @Transactional
    public void incrementImpressions(String id) {
        logger.debug("Incrementing impressions for advertisement with id: {}", id);
        
        validateId(id);
        IklanModel iklan = findIklanById(id);
        iklan.setImpressions(iklan.getImpressions() + 1);
        iklanRepository.save(iklan);
    }
    
    @Transactional
    public void incrementClicks(String id) {
        logger.debug("Incrementing clicks for advertisement with id: {}", id);
        
        validateId(id);
        IklanModel iklan = findIklanById(id);
        iklan.setClicks(iklan.getClicks() + 1);
        iklanRepository.save(iklan);
    }

    @Override
    @Cacheable(value = "publicAdvertisementsCache", key = "{#position, #limit}")
    @CacheEvict(value = "advertisementsCache", allEntries = true)
    public IklanResponseDTO getPublicAdvertisements(String position, Integer limit) {
        logger.debug("Fetching public advertisements with position: {}, limit: {}", position, limit); 
        int maxResults = (limit == null || limit < 1) ? 3 : limit;
        
        Specification<IklanModel> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("status"), IklanStatus.ACTIVE));  
            LocalDateTime now = LocalDateTime.now();
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("startDate"), now));
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("endDate"), now));
            
            if (position != null && !position.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("position"), position));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        
        // Fetch a limited number of advertisements
        Pageable pageable = PageRequest.of(0, maxResults, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<IklanModel> advertisementsPage = iklanRepository.findAll(spec, pageable);
        
        logger.debug("Found {} public advertisements", advertisementsPage.getNumberOfElements());
        
        // Map to simplified DTOs for public consumption
        List<IklanDTO> publicAdDTOs = advertisementsPage.getContent().stream()
                .map(this::mapToPublicDTO)
                .collect(Collectors.toList());
        
        // Create response without pagination
        return IklanResponseDTO.builder()
                .code(HttpStatus.OK.value())
                .success(true)
                .message("Daftar iklan berhasil diambil")
                .data(Map.of("advertisements", publicAdDTOs))
                .build();
    }

    // Helper method to map IklanModel to simplified public DTO
    private IklanDTO mapToPublicDTO(IklanModel model) {
        return IklanDTO.builder()
                .id(model.getId())
                .title(model.getTitle())
                .imageUrl(model.getImageUrl())
                .clickUrl(model.getClickUrl())
                .build();
    }
    
// extra

    // Validation methods
    private void validateId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID iklan tidak boleh kosong");
        }
    }
    
    private void validateStatus(IklanStatus status) {
        if (status == null) {
            throw new InvalidStatusException("Status iklan tidak boleh kosong");
        }
        
        // Validate that status is only ACTIVE or INACTIVE (not EXPIRED) for status updates
        if (status != IklanStatus.ACTIVE && status != IklanStatus.INACTIVE) {
            throw new InvalidStatusException("Status harus berupa 'active' atau 'inactive'");
        }
    }
    
    private void validateAdvertisementDTO(IklanDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Data iklan tidak boleh kosong");
        }
        
        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Judul iklan tidak boleh kosong");
        }
        
        if (dto.getImageUrl() == null || dto.getImageUrl().trim().isEmpty()) {
            throw new IllegalArgumentException("URL gambar iklan tidak boleh kosong");
        }
        
        if (dto.getStartDate() == null) {
            throw new IllegalArgumentException("Tanggal mulai iklan tidak boleh kosong");
        }
        
        if (dto.getEndDate() == null) {
            throw new IllegalArgumentException("Tanggal selesai iklan tidak boleh kosong");
        }
        
        // Validate end date is after start date
        if (!dto.getEndDate().isAfter(dto.getStartDate())) {
            throw new IllegalArgumentException("Tanggal selesai harus setelah tanggal mulai");
        }
    }
    
    private void sanitizeDTO(IklanDTO dto) {
        // Call DTO's sanitize method if it exists
        try {
            dto.getClass().getMethod("sanitize").invoke(dto);
        } catch (Exception e) {
            // Method doesn't exist or couldn't be called, just log and continue
            logger.debug("Sanitize method not available for DTO: {}", e.getMessage());
        }
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
            .description(dto.getDescription())
            .imageUrl(dto.getImageUrl())
            .startDate(dto.getStartDate())
            .endDate(dto.getEndDate())
            .status(dto.getStatus() != null ? dto.getStatus() : IklanStatus.INACTIVE)
            .clickUrl(dto.getClickUrl())
            .position(dto.getPosition())
            .impressions(dto.getImpressions() != null ? dto.getImpressions() : 0)
            .clicks(dto.getClicks() != null ? dto.getClicks() : 0)
            .build();
    }
    
    private void updateIklanFromDTO(IklanModel iklan, IklanDTO dto) {
        iklan.setTitle(dto.getTitle());
        iklan.setDescription(dto.getDescription());
        iklan.setImageUrl(dto.getImageUrl());
        iklan.setStartDate(dto.getStartDate());
        iklan.setEndDate(dto.getEndDate());
        iklan.setStatus(dto.getStatus());
        iklan.setClickUrl(dto.getClickUrl());
        iklan.setPosition(dto.getPosition());
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
                .data(dto)
                .build();
    }
    
    private IklanDTO mapToDTO(IklanModel model) {
        return IklanDTO.builder()
            .id(model.getId())
            .title(model.getTitle())
            .description(model.getDescription())
            .imageUrl(model.getImageUrl())
            .startDate(model.getStartDate())
            .endDate(model.getEndDate())
            .status(model.getStatus())
            .clickUrl(model.getClickUrl())
            .position(model.getPosition())
            .impressions(model.getImpressions())
            .clicks(model.getClicks())
            .createdAt(model.getCreatedAt())
            .updatedAt(model.getUpdatedAt())
            .build();
    }
}