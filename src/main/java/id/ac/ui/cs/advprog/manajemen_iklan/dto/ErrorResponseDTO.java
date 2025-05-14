package id.ac.ui.cs.advprog.manajemen_iklan.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDTO {
    private int code;
    private boolean success;
    private String message;
    private Map<String, String> errors;
}