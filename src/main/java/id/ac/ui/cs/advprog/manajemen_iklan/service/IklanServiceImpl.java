package id.ac.ui.cs.advprog.manajemen_iklan.service;
import id.ac.ui.cs.advprog.manajemen_iklan.dto.IklanDTO;
import id.ac.ui.cs.advprog.manajemen_iklan.dto.IklanResponseDTO;
import id.ac.ui.cs.advprog.manajemen_iklan.enums.IklanStatus;
import id.ac.ui.cs.advprog.manajemen_iklan.model.IklanModel;
import id.ac.ui.cs.advprog.manajemen_iklan.repository.IklanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
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
        
        logger.debug("Fetching advertisements with filters: status={}, startDateFrom={}, startDateTo={}, endDateFrom={}, endDateTo={}, search={}, page={}, limit={}",
                statusParam, startDateFrom, startDateTo, endDateFrom, endDateTo, search, page, limit);
        
        // Set default values if null
        int pageNumber = (page == null || page < 1) ? 0 : page - 1;
        int pageSize = (limit == null || limit < 1) ? 10 : Math.min(limit, 50);
        
        // Parse status if provided
        IklanStatus status = null;
        if (statusParam != null && !statusParam.isEmpty()) {
            try {
                // Improved case insensitive parsing
                switch(statusParam.toLowerCase()) {
                case "active":
                        status = IklanStatus.ACTIVE;
                        break;
                case "inactive":
                        status = IklanStatus.INACTIVE;
                        break;
                case "expired":
                        status = IklanStatus.EXPIRED;
                        break;
                default:
                    logger.warn("Invalid status value provided: {}", statusParam);
                    status = null;
                }
            } catch (IllegalArgumentException e) {
                logger.error("Error parsing status value: {}", statusParam, e);
                status = null;
            }
        }
        
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
        
        // Map entities to DTOs
        List<IklanDTO> advertisementDTOs = advertisementsPage.getContent().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        
        // Create pagination info
        IklanResponseDTO.PaginationDTO paginationDTO = IklanResponseDTO.PaginationDTO.builder()
                .currentPage(pageNumber + 1)
                .totalPages(advertisementsPage.getTotalPages())
                .totalItems(advertisementsPage.getTotalElements())
                .limit(pageSize)
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
                .message("Daftar iklan berhasil diambil")
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