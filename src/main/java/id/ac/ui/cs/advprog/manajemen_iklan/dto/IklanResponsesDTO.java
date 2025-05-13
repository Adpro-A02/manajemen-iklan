package id.ac.ui.cs.advprog.manajemen_iklan.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IklanResponsesDTO {
    private int code;
    private boolean success;
    private String message;
    private IklanDataDTO data;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IklanDataDTO {
        private List<IklanDTO> advertisements;
        private PaginationDTO pagination;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaginationDTO {
        private int currentPage;
        private int totalPages;
        private long totalItems;
        private int limit;
    }
}
