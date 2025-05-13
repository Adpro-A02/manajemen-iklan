package id.ac.ui.cs.advprog.manajemen_iklan.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import id.ac.ui.cs.advprog.manajemen_iklan.enums.IklanStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IklanDTO {
    private String id;
    private String title;
    private String imageUrl;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private IklanStatus status;
    private String clickUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
