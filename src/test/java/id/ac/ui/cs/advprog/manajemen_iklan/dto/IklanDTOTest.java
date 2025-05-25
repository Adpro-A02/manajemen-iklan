package id.ac.ui.cs.advprog.manajemen_iklan.dto;

import id.ac.ui.cs.advprog.manajemen_iklan.enums.IklanStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class IklanDTOTest {

    private IklanDTO iklanDTO;

    @BeforeEach
    void setUp() {
        iklanDTO = new IklanDTO();
    }

    @Test
    void testIklanDTOCreation() {
        // Given
        String id = "test-id";
        String title = "Test Title";
        String description = "Test Description";
        String imageUrl = "https://example.com/image.jpg";
        String clickUrl = "https://example.com";
        String position = "banner";
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusDays(30);
        IklanStatus status = IklanStatus.ACTIVE;
        Integer impressions = 100;
        Integer clicks = 10;

        // When
        iklanDTO.setId(id);
        iklanDTO.setTitle(title);
        iklanDTO.setDescription(description);
        iklanDTO.setImageUrl(imageUrl);
        iklanDTO.setClickUrl(clickUrl);
        iklanDTO.setPosition(position);
        iklanDTO.setStartDate(startDate);
        iklanDTO.setEndDate(endDate);
        iklanDTO.setStatus(status);
        iklanDTO.setImpressions(impressions);
        iklanDTO.setClicks(clicks);

        // Then
        assertEquals(id, iklanDTO.getId());
        assertEquals(title, iklanDTO.getTitle());
        assertEquals(description, iklanDTO.getDescription());
        assertEquals(imageUrl, iklanDTO.getImageUrl());
        assertEquals(clickUrl, iklanDTO.getClickUrl());
        assertEquals(position, iklanDTO.getPosition());
        assertEquals(startDate, iklanDTO.getStartDate());
        assertEquals(endDate, iklanDTO.getEndDate());
        assertEquals(status, iklanDTO.getStatus());
        assertEquals(impressions, iklanDTO.getImpressions());
        assertEquals(clicks, iklanDTO.getClicks());
    }

    @Test
    void testBuilderPattern() {
        // Given
        LocalDateTime now = LocalDateTime.now();

        // When
        IklanDTO dto = IklanDTO.builder()
                .id("builder-test")
                .title("Builder Test")
                .description("Test Description")
                .imageUrl("https://example.com/image.jpg")
                .clickUrl("https://example.com")
                .position("banner")
                .startDate(now)
                .endDate(now.plusDays(30))
                .status(IklanStatus.ACTIVE)
                .impressions(50)
                .clicks(5)
                .build();

        // Then
        assertEquals("builder-test", dto.getId());
        assertEquals("Builder Test", dto.getTitle());
        assertEquals(50, dto.getImpressions());
        assertEquals(5, dto.getClicks());
    }

    @Test
    void testDefaultValues() {
        // When
        IklanDTO dto = IklanDTO.builder().build();

        // Then
        assertEquals(0, dto.getImpressions());
        assertEquals(0, dto.getClicks());
    }

    @Test
    void testIsEndDateAfterStartDate_ValidDates() {
        // Given
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusDays(30);
        
        iklanDTO.setStartDate(startDate);
        iklanDTO.setEndDate(endDate);

        // When
        boolean result = iklanDTO.isEndDateAfterStartDate();

        // Then
        assertTrue(result);
    }

    @Test
    void testIsEndDateAfterStartDate_InvalidDates() {
        // Given
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.minusDays(1);
        
        iklanDTO.setStartDate(startDate);
        iklanDTO.setEndDate(endDate);

        // When
        boolean result = iklanDTO.isEndDateAfterStartDate();

        // Then
        assertFalse(result);
    }

    @Test
    void testIsEndDateAfterStartDate_NullDates() {
        // Given
        iklanDTO.setStartDate(null);
        iklanDTO.setEndDate(null);

        // When
        boolean result = iklanDTO.isEndDateAfterStartDate();

        // Then
        assertTrue(result); // Should return true when dates are null
    }

    @Test
    void testIsEndDateAfterStartDate_NullStartDate() {
        // Given
        iklanDTO.setStartDate(null);
        iklanDTO.setEndDate(LocalDateTime.now());

        // When
        boolean result = iklanDTO.isEndDateAfterStartDate();

        // Then
        assertTrue(result);
    }

    @Test
    void testIsEndDateAfterStartDate_NullEndDate() {
        // Given
        iklanDTO.setStartDate(LocalDateTime.now());
        iklanDTO.setEndDate(null);

        // When
        boolean result = iklanDTO.isEndDateAfterStartDate();

        // Then
        assertTrue(result);
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        
        IklanDTO dto1 = IklanDTO.builder()
                .id("test-id")
                .title("Test Title")
                .startDate(now)
                .build();

        IklanDTO dto2 = IklanDTO.builder()
                .id("test-id")
                .title("Test Title")
                .startDate(now)
                .build();

        // Then
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testToString() {
        // Given
        IklanDTO dto = IklanDTO.builder()
                .id("test-id")
                .title("Test Title")
                .build();

        // When
        String result = dto.toString();

        // Then
        assertNotNull(result);
        assertTrue(result.contains("test-id"));
        assertTrue(result.contains("Test Title"));
    }

    @Test
    void testStatusUpdateValidationInterface() {
        // This test verifies that the StatusUpdateValidation interface exists
        // and can be used for validation groups
        assertNotNull(IklanDTO.StatusUpdateValidation.class);
    }
}