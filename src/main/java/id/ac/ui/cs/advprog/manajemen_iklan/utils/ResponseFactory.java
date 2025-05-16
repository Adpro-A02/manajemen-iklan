package id.ac.ui.cs.advprog.manajemen_iklan.utils;

import id.ac.ui.cs.advprog.manajemen_iklan.dto.IklanDTO;
import id.ac.ui.cs.advprog.manajemen_iklan.dto.IklanResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ResponseFactory {
    
    public IklanResponseDTO createListResponse(Page<IklanDTO> page, int currentPage, int limit, String message) {
        // Create pagination info
        IklanResponseDTO.PaginationDTO paginationDTO = IklanResponseDTO.PaginationDTO.builder()
                .currentPage(currentPage)
                .totalPages(page.getTotalPages())
                .totalItems(page.getTotalElements())
                .limit(limit)
                .build();
        
        // Create data object
        IklanResponseDTO.IklanDataDTO dataDTO = IklanResponseDTO.IklanDataDTO.builder()
                .advertisements(page.getContent())
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
    
    public IklanResponseDTO createDetailResponse(IklanDTO dto, int statusCode, String message) {
        return IklanResponseDTO.builder()
                .code(statusCode)
                .success(true)
                .message(message)
                .data(dto)
                .build();
    }
    
    public IklanResponseDTO createPublicListResponse(List<IklanDTO> dtos, String message) {
        return IklanResponseDTO.builder()
                .code(HttpStatus.OK.value())
                .success(true)
                .message(message)
                .data(Map.of("advertisements", dtos))
                .build();
    }
    
    public IklanResponseDTO createStatusUpdateResponse(String id, String status, Object updatedAt) {
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("id", id);
        responseData.put("status", status);
        responseData.put("updatedAt", updatedAt);
        
        return IklanResponseDTO.builder()
            .code(HttpStatus.OK.value())
            .success(true)
            .message("Status iklan berhasil diperbarui")
            .data(responseData)
            .build();
    }
    
    public IklanResponseDTO createDeleteResponse() {
        return IklanResponseDTO.builder()
                .code(HttpStatus.OK.value())
                .success(true)
                .message("Iklan berhasil dihapus")
                .build();
    }
}