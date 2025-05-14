package id.ac.ui.cs.advprog.manajemen_iklan.dto;

import id.ac.ui.cs.advprog.manajemen_iklan.enums.IklanStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IklanDTO {
    
    private String id;
    
    @NotBlank(message = "Title is required")
    private String title;
    
    @NotBlank(message = "Image URL is required")
    @Pattern(regexp = "^(https?|ftp)://.*$", message = "Image URL must be a valid URL")
    private String imageUrl;
    
    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;
    
    @NotNull(message = "End date is required")
    @FutureOrPresent(message = "End date must be in the future or present")
    private LocalDateTime endDate;
    
    private IklanStatus status;
    
    @Pattern(regexp = "^(https?|ftp)://.*$", message = "Click URL must be a valid URL")
    private String clickUrl;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}