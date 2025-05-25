package id.ac.ui.cs.advprog.manajemen_iklan.service.validation;

import id.ac.ui.cs.advprog.manajemen_iklan.dto.IklanDTO;
import id.ac.ui.cs.advprog.manajemen_iklan.enums.IklanStatus;
import id.ac.ui.cs.advprog.manajemen_iklan.exception.InvalidStatusException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class IklanValidatorTest {

    private IklanValidator validator;
    private IklanDTO validIklanDTO;

    @BeforeEach
    void setUp() {
        validator = new IklanValidator();
        
        // Make sure startDate is in the future for new ads (id == null)
        validIklanDTO = IklanDTO.builder()
                .title("Valid Title")
                .description("Valid Description")
                .imageUrl("https://example.com/image.jpg")
                .clickUrl("https://example.com")
                .position("homepage_top") // Use valid position
                .startDate(LocalDateTime.now().plusDays(1)) // Future date
                .endDate(LocalDateTime.now().plusDays(30))
                .status(IklanStatus.ACTIVE)
                .build();
    }

    @Test
    void testValidateId_ValidId() {
        assertDoesNotThrow(() -> validator.validateId("valid-id"));
    }

    @Test
    void testValidateId_NullId() {
        assertThrows(IllegalArgumentException.class, () -> validator.validateId(null));
    }

    @Test
    void testValidateId_EmptyId() {
        assertThrows(IllegalArgumentException.class, () -> validator.validateId(""));
    }

    @Test
    void testValidateAdvertisementDTO_Valid() {
        // Should not throw any exception for valid DTO
        assertDoesNotThrow(() -> validator.validateAdvertisementDTO(validIklanDTO));
    }

    @Test
    void testValidateAdvertisementDTO_ValidForExistingAd() {
        // For existing ads (with ID), startDate can be in the past
        validIklanDTO.setId("existing-id");
        validIklanDTO.setStartDate(LocalDateTime.now().minusDays(1)); // Past date is OK for existing ads
        
        assertDoesNotThrow(() -> validator.validateAdvertisementDTO(validIklanDTO));
    }

    @Test
    void testValidateAdvertisementDTO_NullDTO() {
        assertThrows(IllegalArgumentException.class, () -> validator.validateAdvertisementDTO(null));
    }

    @Test
    void testValidateAdvertisementDTO_NullTitle() {
        validIklanDTO.setTitle(null);
        assertThrows(IllegalArgumentException.class, () -> validator.validateAdvertisementDTO(validIklanDTO));
    }

    @Test
    void testValidateAdvertisementDTO_EmptyTitle() {
        validIklanDTO.setTitle("");
        assertThrows(IllegalArgumentException.class, () -> validator.validateAdvertisementDTO(validIklanDTO));
    }

    @Test
    void testValidateAdvertisementDTO_TitleTooLong() {
        validIklanDTO.setTitle("A".repeat(101));
        assertThrows(IllegalArgumentException.class, () -> validator.validateAdvertisementDTO(validIklanDTO));
    }

    @Test
    void testValidateAdvertisementDTO_EndDateBeforeStartDate() {
        validIklanDTO.setEndDate(LocalDateTime.now().minusDays(1));
        assertThrows(IllegalArgumentException.class, () -> validator.validateAdvertisementDTO(validIklanDTO));
    }

    @Test
    void testValidateAdvertisementDTO_StartDateInPastForNewAd() {
        // For new ads (id == null), start date must be in future
        validIklanDTO.setStartDate(LocalDateTime.now().minusDays(1));
        assertThrows(IllegalArgumentException.class, () -> validator.validateAdvertisementDTO(validIklanDTO));
    }

    @Test
    void testValidateAdvertisementDTO_NullImageUrl() {
        validIklanDTO.setImageUrl(null);
        assertThrows(IllegalArgumentException.class, () -> validator.validateAdvertisementDTO(validIklanDTO));
    }

    @Test
    void testValidateAdvertisementDTO_EmptyImageUrl() {
        validIklanDTO.setImageUrl("");
        assertThrows(IllegalArgumentException.class, () -> validator.validateAdvertisementDTO(validIklanDTO));
    }

    @Test
    void testValidateAdvertisementDTO_DescriptionTooLong() {
        validIklanDTO.setDescription("A".repeat(501));
        assertThrows(IllegalArgumentException.class, () -> validator.validateAdvertisementDTO(validIklanDTO));
    }

    @Test
    void testValidateAdvertisementDTO_InvalidPosition() {
        validIklanDTO.setPosition("invalid_position");
        assertThrows(IllegalArgumentException.class, () -> validator.validateAdvertisementDTO(validIklanDTO));
    }

    @Test
    void testValidateAdvertisementDTO_ValidPositions() {
        String[] validPositions = {"homepage_top", "homepage_middle", "homepage_bottom"};
        
        for (String position : validPositions) {
            validIklanDTO.setPosition(position);
            assertDoesNotThrow(() -> validator.validateAdvertisementDTO(validIklanDTO));
        }
    }

    @Test
    void testValidateStatus_ValidStatus() {
        assertDoesNotThrow(() -> validator.validateStatus(IklanStatus.ACTIVE));
        assertDoesNotThrow(() -> validator.validateStatus(IklanStatus.INACTIVE));
    }

    @Test
    void testValidateStatus_NullStatus() {
        assertThrows(InvalidStatusException.class, () -> validator.validateStatus(null));
    }

    @Test
    void testParseStatus_ValidString() {
        assertEquals(IklanStatus.ACTIVE, validator.parseStatus("active"));
        assertEquals(IklanStatus.INACTIVE, validator.parseStatus("inactive"));
        assertEquals(IklanStatus.EXPIRED, validator.parseStatus("expired"));
    }

    @Test
    void testParseStatus_InvalidString() {
        // Based on the implementation, invalid strings return null
        assertNull(validator.parseStatus("invalid"));
    }

    @Test
    void testParseStatus_NullString() {
        assertNull(validator.parseStatus(null));
    }

    @Test
    void testParseStatus_EmptyString() {
        assertNull(validator.parseStatus(""));
    }

    @Test
    void testParseStatus_CaseInsensitive() {
        assertEquals(IklanStatus.ACTIVE, validator.parseStatus("ACTIVE"));
        assertEquals(IklanStatus.ACTIVE, validator.parseStatus("Active"));
        assertEquals(IklanStatus.INACTIVE, validator.parseStatus("INACTIVE"));
    }
}