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
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IklanServiceImpl implements IklanService {
    
    private final IklanRepository iklanRepository;
    
    @Override
    public IklanResponseDTO getAllAdvertisements(
            Integer page,
            Integer limit,
            String statusParam,
            LocalDateTime startDateFrom,
            LocalDateTime startDateTo,
            LocalDateTime endDateFrom,
            LocalDateTime endDateTo,
            String search) {
        
        // Set default values if null
        int pageNumber = (page == null || page < 1) ? 0 : page - 1;
        int pageSize = (limit == null || limit < 1) ? 10 : Math.min(limit, 50);
        
        // Parse status if provided
        IklanStatus status = null;
        if (statusParam != null && !statusParam.isEmpty()) {
            try {
                status = IklanStatus.valueOf(statusParam.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Handle invalid status value
                // For now, im setting it to null
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
