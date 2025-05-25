package id.ac.ui.cs.advprog.manajemen_iklan.model;

import id.ac.ui.cs.advprog.manajemen_iklan.enums.IklanStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class IklanModelTest {

    private IklanModel iklanModel;

    @BeforeEach
    void setUp() {
        iklanModel = new IklanModel();
    }

    @Test
    void testIklanModelCreation() {
        String title = "Test Advertisement";
        String description = "Test Description";
        String imageUrl = "https://example.com/image.jpg";
        String clickUrl = "https://example.com";
        String position = "banner";
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusDays(30);

        iklanModel.setTitle(title);
        iklanModel.setDescription(description);
        iklanModel.setImageUrl(imageUrl);
        iklanModel.setClickUrl(clickUrl);
        iklanModel.setPosition(position);
        iklanModel.setStartDate(startDate);
        iklanModel.setEndDate(endDate);
        iklanModel.setStatus(IklanStatus.ACTIVE);

        assertEquals(title, iklanModel.getTitle());
        assertEquals(description, iklanModel.getDescription());
        assertEquals(imageUrl, iklanModel.getImageUrl());
        assertEquals(clickUrl, iklanModel.getClickUrl());
        assertEquals(position, iklanModel.getPosition());
        assertEquals(startDate, iklanModel.getStartDate());
        assertEquals(endDate, iklanModel.getEndDate());
        assertEquals(IklanStatus.ACTIVE, iklanModel.getStatus());
    }

    @Test
    void testDefaultValues() {
        // Test default values from @Builder.Default annotations
        assertEquals(Integer.valueOf(0), iklanModel.getImpressions());
        assertEquals(Integer.valueOf(0), iklanModel.getClicks());
        // Note: Status doesn't have a default in the model, so it should be null
        assertNull(iklanModel.getStatus());
    }

    @Test
    void testIncrementImpressions() {
        assertEquals(Integer.valueOf(0), iklanModel.getImpressions());
        iklanModel.setImpressions(iklanModel.getImpressions() + 1);
        assertEquals(Integer.valueOf(1), iklanModel.getImpressions());
    }

    @Test
    void testIncrementClicks() {
        assertEquals(Integer.valueOf(0), iklanModel.getClicks());
        iklanModel.setClicks(iklanModel.getClicks() + 1);
        assertEquals(Integer.valueOf(1), iklanModel.getClicks());
    }

    @Test
    void testStatusTransitions() {
        iklanModel.setStatus(IklanStatus.INACTIVE);
        assertEquals(IklanStatus.INACTIVE, iklanModel.getStatus());

        iklanModel.setStatus(IklanStatus.ACTIVE);
        assertEquals(IklanStatus.ACTIVE, iklanModel.getStatus());

        iklanModel.setStatus(IklanStatus.EXPIRED);
        assertEquals(IklanStatus.EXPIRED, iklanModel.getStatus());
    }

    @Test
    void testBuilder() {
        LocalDateTime now = LocalDateTime.now();
        IklanModel iklan = IklanModel.builder()
                .title("Builder Test")
                .description("Test Description")
                .imageUrl("https://example.com/image.jpg")
                .clickUrl("https://example.com")
                .position("banner")
                .startDate(now)
                .endDate(now.plusDays(30))
                .status(IklanStatus.ACTIVE)
                .impressions(100)
                .clicks(10)
                .build();

        assertEquals("Builder Test", iklan.getTitle());
        assertEquals("Test Description", iklan.getDescription());
        assertEquals(Integer.valueOf(100), iklan.getImpressions());
        assertEquals(Integer.valueOf(10), iklan.getClicks());
        assertEquals(IklanStatus.ACTIVE, iklan.getStatus());
    }

    @Test
    void testEqualsAndHashCode() {
        IklanModel iklan1 = IklanModel.builder()
                .id("test-id")
                .title("Test Title")
                .build();

        IklanModel iklan2 = IklanModel.builder()
                .id("test-id")
                .title("Test Title")
                .build();

        assertEquals(iklan1, iklan2);
        assertEquals(iklan1.hashCode(), iklan2.hashCode());
    }
}