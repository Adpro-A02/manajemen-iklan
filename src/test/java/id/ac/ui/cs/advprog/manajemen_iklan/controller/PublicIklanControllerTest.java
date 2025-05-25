package id.ac.ui.cs.advprog.manajemen_iklan.controller;

import id.ac.ui.cs.advprog.manajemen_iklan.dto.IklanDTO;
import id.ac.ui.cs.advprog.manajemen_iklan.dto.IklanResponseDTO;
import id.ac.ui.cs.advprog.manajemen_iklan.service.IklanService;
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

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PublicIklanControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IklanService iklanService;

    @InjectMocks
    private PublicIklanController publicIklanController;

    private IklanResponseDTO testResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(publicIklanController).build();
        
        IklanDTO testIklanDTO = IklanDTO.builder()
                .id("test-id")
                .title("Test Advertisement")
                .imageUrl("https://example.com/image.jpg")
                .clickUrl("https://example.com")
                .build();

        testResponse = IklanResponseDTO.builder()
                .code(HttpStatus.OK.value())
                .success(true)
                .message("Daftar iklan berhasil diambil")
                .data(Map.of("advertisements", List.of(testIklanDTO)))
                .build();
    }

    @Test
    void testGetPublicAdvertisements() throws Exception {
        // Given - Use any() matchers to avoid stubbing problems
        when(iklanService.getPublicAdvertisements(any(), any()))
                .thenReturn(testResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/public/advertisements")
                        .param("position", "banner")
                        .param("limit", "3"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetPublicAdvertisements_WithoutParams() throws Exception {
        // Given - When no params provided, use any() matchers
        when(iklanService.getPublicAdvertisements(any(), any()))
                .thenReturn(testResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/public/advertisements"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetPublicAdvertisements_InvalidPosition() throws Exception {
        // Given - Use any() matchers
        when(iklanService.getPublicAdvertisements(any(), any()))
                .thenReturn(testResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/public/advertisements")
                        .param("position", "invalid_position")
                        .param("limit", "5"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetPublicAdvertisements_LimitExceedsMaximum() throws Exception {
        // Given - Use any() matchers
        when(iklanService.getPublicAdvertisements(any(), any()))
                .thenReturn(testResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/public/advertisements")
                        .param("position", "banner")
                        .param("limit", "15"))
                .andExpect(status().isOk());
    }
}