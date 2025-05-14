package id.ac.ui.cs.advprog.manajemen_iklan.dto;

import id.ac.ui.cs.advprog.manajemen_iklan.enums.IklanStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IklanDTO {
    
    private String id;
    
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;
    
    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;
    
    @NotBlank(message = "Image URL is required")
    @Pattern(regexp = "^(https?|ftp)://.*$", message = "Image URL must be a valid URL")
    @URL(message = "Image URL must be a valid URL")
    private String imageUrl;
    
    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;
    
    @NotNull(message = "End date is required")
    @FutureOrPresent(message = "End date must be in the future or present")
    private LocalDateTime endDate;
    
    private IklanStatus status;
    
    @Pattern(regexp = "^(https?|ftp)://.*$", message = "Click URL must be a valid URL", flags = Pattern.Flag.CASE_INSENSITIVE)
    @URL(message = "Click URL must be a valid URL")
    private String clickUrl;
    
    @Size(max = 50, message = "Position cannot exceed 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_\\-\\s]*$", message = "Position can only contain alphanumeric characters, spaces, hyphens, and underscores")
    private String position;
    
    @Min(value = 0, message = "Impressions cannot be negative")
    @Max(value = Integer.MAX_VALUE, message = "Impressions value is too large")
    @Builder.Default
    private Integer impressions = 0;
    
    @Min(value = 0, message = "Clicks cannot be negative")
    @Max(value = Integer.MAX_VALUE, message = "Clicks value is too large")
    @Builder.Default
    private Integer clicks = 0;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // Additional validation method to ensure end date is after start date
    public boolean isEndDateAfterStartDate() {
        if (startDate != null && endDate != null) {
            return endDate.isAfter(startDate);
        }
        return true;
    }
}