package id.ac.ui.cs.advprog.manajemen_iklan.controller;

import id.ac.ui.cs.advprog.manajemen_iklan.dto.IklanDTO;
import id.ac.ui.cs.advprog.manajemen_iklan.dto.IklanResponseDTO;
import id.ac.ui.cs.advprog.manajemen_iklan.enums.IklanStatus;
import id.ac.ui.cs.advprog.manajemen_iklan.service.IklanService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class IklanControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IklanService iklanService;

    @InjectMocks
    private IklanController iklanController;

    private ObjectMapper objectMapper;
    private IklanDTO testIklanDTO;
    private IklanResponseDTO testResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(iklanController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        
        testIklanDTO = IklanDTO.builder()
                .id("test-id")
                .title("Test Advertisement")
                .description("Test Description")
                .imageUrl("https://example.com/image.jpg")
                .clickUrl("https://example.com")
                .position("banner")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30))
                .status(IklanStatus.ACTIVE)
                .build();

        testResponse = IklanResponseDTO.builder()
                .code(HttpStatus.OK.value())
                .success(true)
                .message("Success")
                .data(testIklanDTO)
                .build();
    }

    @Test
    void testGetAllAdvertisements() throws Exception {
        // Given
        when(iklanService.getAllAdvertisements(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(testResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/advertisements")
                        .param("page", "1")
                        .param("limit", "10")
                        .param("status", "active"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetAdvertisementById() throws Exception {
        // Given
        when(iklanService.getAdvertisementById("test-id")).thenReturn(testResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/advertisements/test-id"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testCreateAdvertisement() throws Exception {
        // Given
        IklanResponseDTO createResponse = IklanResponseDTO.builder()
                .code(HttpStatus.CREATED.value())
                .success(true)
                .message("Iklan berhasil ditambahkan")
                .data(testIklanDTO)
                .build();

        when(iklanService.createAdvertisement(any(IklanDTO.class))).thenReturn(createResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/advertisements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testIklanDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testUpdateAdvertisement() throws Exception {
        // Given
        when(iklanService.updateAdvertisement(eq("test-id"), any(IklanDTO.class)))
                .thenReturn(testResponse);

        // When & Then
        mockMvc.perform(put("/api/v1/advertisements/test-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testIklanDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testDeleteAdvertisement() throws Exception {
        // Given
        IklanResponseDTO deleteResponse = IklanResponseDTO.builder()
                .code(HttpStatus.OK.value())
                .success(true)
                .message("Iklan berhasil dihapus")
                .build();

        when(iklanService.deleteAdvertisement("test-id")).thenReturn(deleteResponse);

        // When & Then
        mockMvc.perform(delete("/api/v1/advertisements/test-id"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testBatchUpdateStatus() throws Exception {
        // Given
        Map<String, Object> request = Map.of(
                "ids", List.of("id1", "id2"),
                "status", "ACTIVE"
        );

        // When & Then
        mockMvc.perform(patch("/api/v1/advertisements/batch-status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted());
    }

    @Test
    void testGenerateReport() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/advertisements/reports/generate")
                        .param("startDate", "2023-01-01T00:00:00")
                        .param("endDate", "2023-12-31T23:59:59"))
                .andExpect(status().isAccepted());
    }
}