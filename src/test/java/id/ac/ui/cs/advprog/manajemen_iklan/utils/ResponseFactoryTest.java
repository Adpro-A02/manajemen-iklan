package id.ac.ui.cs.advprog.manajemen_iklan.utils;

import id.ac.ui.cs.advprog.manajemen_iklan.dto.IklanDTO;
import id.ac.ui.cs.advprog.manajemen_iklan.dto.IklanResponseDTO;
import id.ac.ui.cs.advprog.manajemen_iklan.enums.IklanStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ResponseFactoryTest {

    private ResponseFactory responseFactory;
    private IklanDTO testIklanDTO;

    @BeforeEach
    void setUp() {
        responseFactory = new ResponseFactory();
        
        testIklanDTO = IklanDTO.builder()
                .id("test-id")
                .title("Test Advertisement")
                .description("Test Description")
                .status(IklanStatus.ACTIVE)
                .build();
    }

    @Test
    void testCreateListResponse() {
        // Given
        List<IklanDTO> dtoList = List.of(testIklanDTO);
        Page<IklanDTO> page = new PageImpl<>(dtoList, PageRequest.of(0, 10), 1);

        // When
        IklanResponseDTO response = responseFactory.createListResponse(page, 1, 10, "Success");

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getCode());
        assertTrue(response.isSuccess());
        assertEquals("Success", response.getMessage());
        assertNotNull(response.getData());
    }

    @Test
    void testCreateDetailResponse() {
        // When
        IklanResponseDTO response = responseFactory.createDetailResponse(
                testIklanDTO, HttpStatus.OK.value(), "Detail retrieved");

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getCode());
        assertTrue(response.isSuccess());
        assertEquals("Detail retrieved", response.getMessage());
        assertEquals(testIklanDTO, response.getData());
    }

    @Test
    void testCreatePublicListResponse() {
        // Given
        List<IklanDTO> dtoList = List.of(testIklanDTO);

        // When
        IklanResponseDTO response = responseFactory.createPublicListResponse(dtoList, "Public ads");

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getCode());
        assertTrue(response.isSuccess());
        assertEquals("Public ads", response.getMessage());
        assertNotNull(response.getData());
    }

    @Test
    void testCreateStatusUpdateResponse() {
        // Given
        LocalDateTime updatedAt = LocalDateTime.now();

        // When
        IklanResponseDTO response = responseFactory.createStatusUpdateResponse(
                "test-id", "active", updatedAt);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getCode());
        assertTrue(response.isSuccess());
        assertEquals("Status iklan berhasil diperbarui", response.getMessage());
        assertNotNull(response.getData());
    }

    @Test
    void testCreateDeleteResponse() {
        // When
        IklanResponseDTO response = responseFactory.createDeleteResponse();

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getCode());
        assertTrue(response.isSuccess());
        assertEquals("Iklan berhasil dihapus", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    void testCreateErrorResponse() {
        // When
        IklanResponseDTO response = responseFactory.createErrorResponse(
                HttpStatus.NOT_FOUND.value(), "Not found");

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getCode());
        assertFalse(response.isSuccess());
        assertEquals("Not found", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    void testCreateCustomResponse() {
        // Given
        Object customData = "Custom data";

        // When
        IklanResponseDTO response = responseFactory.createCustomResponse(
                customData, HttpStatus.OK.value(), "Custom response");

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getCode());
        assertTrue(response.isSuccess());
        assertEquals("Custom response", response.getMessage());
        assertEquals(customData, response.getData());
    }
}