package id.ac.ui.cs.advprog.manajemen_iklan.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IklanResponseDTOTest {

    @Test
    void testIklanResponseDTOCreation() {
        // Given
        int code = 200;
        boolean success = true;
        String message = "Success";
        Object data = "Test Data";

        // When
        IklanResponseDTO response = IklanResponseDTO.builder()
                .code(code)
                .success(success)
                .message(message)
                .data(data)
                .build();

        // Then
        assertEquals(code, response.getCode());
        assertEquals(success, response.isSuccess());
        assertEquals(message, response.getMessage());
        assertEquals(data, response.getData());
    }

    @Test
    void testPaginationDTO() {
        // Given
        int currentPage = 1;
        int totalPages = 5;
        long totalItems = 50L;
        int limit = 10;

        // When
        IklanResponseDTO.PaginationDTO pagination = IklanResponseDTO.PaginationDTO.builder()
                .currentPage(currentPage)
                .totalPages(totalPages)
                .totalItems(totalItems)
                .limit(limit)
                .build();

        // Then
        assertEquals(currentPage, pagination.getCurrentPage());
        assertEquals(totalPages, pagination.getTotalPages());
        assertEquals(totalItems, pagination.getTotalItems());
        assertEquals(limit, pagination.getLimit());
    }

    @Test
    void testIklanDataDTO() {
        // Given
        List<IklanDTO> advertisements = List.of(
                IklanDTO.builder().id("1").title("Ad 1").build(),
                IklanDTO.builder().id("2").title("Ad 2").build()
        );
        
        IklanResponseDTO.PaginationDTO pagination = IklanResponseDTO.PaginationDTO.builder()
                .currentPage(1)
                .totalPages(1)
                .totalItems(2L)
                .limit(10)
                .build();

        // When
        IklanResponseDTO.IklanDataDTO dataDTO = IklanResponseDTO.IklanDataDTO.builder()
                .advertisements(advertisements)
                .pagination(pagination)
                .build();

        // Then
        assertEquals(advertisements, dataDTO.getAdvertisements());
        assertEquals(pagination, dataDTO.getPagination());
        assertEquals(2, dataDTO.getAdvertisements().size());
    }

    @Test
    void testCompleteResponseWithNestedData() {
        // Given
        List<IklanDTO> advertisements = List.of(
                IklanDTO.builder().id("1").title("Ad 1").build()
        );
        
        IklanResponseDTO.PaginationDTO pagination = IklanResponseDTO.PaginationDTO.builder()
                .currentPage(1)
                .totalPages(1)
                .totalItems(1L)
                .limit(10)
                .build();

        IklanResponseDTO.IklanDataDTO dataDTO = IklanResponseDTO.IklanDataDTO.builder()
                .advertisements(advertisements)
                .pagination(pagination)
                .build();

        // When
        IklanResponseDTO response = IklanResponseDTO.builder()
                .code(200)
                .success(true)
                .message("Data retrieved successfully")
                .data(dataDTO)
                .build();

        // Then
        assertEquals(200, response.getCode());
        assertTrue(response.isSuccess());
        assertEquals("Data retrieved successfully", response.getMessage());
        assertNotNull(response.getData());
        assertTrue(response.getData() instanceof IklanResponseDTO.IklanDataDTO);
        
        IklanResponseDTO.IklanDataDTO responseData = (IklanResponseDTO.IklanDataDTO) response.getData();
        assertEquals(1, responseData.getAdvertisements().size());
        assertEquals("Ad 1", responseData.getAdvertisements().get(0).getTitle());
    }

    @Test
    void testErrorResponse() {
        // When
        IklanResponseDTO response = IklanResponseDTO.builder()
                .code(404)
                .success(false)
                .message("Resource not found")
                .data(null)
                .build();

        // Then
        assertEquals(404, response.getCode());
        assertFalse(response.isSuccess());
        assertEquals("Resource not found", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        IklanResponseDTO response1 = IklanResponseDTO.builder()
                .code(200)
                .success(true)
                .message("Success")
                .data("Test")
                .build();

        IklanResponseDTO response2 = IklanResponseDTO.builder()
                .code(200)
                .success(true)
                .message("Success")
                .data("Test")
                .build();

        // Then
        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
    }
}