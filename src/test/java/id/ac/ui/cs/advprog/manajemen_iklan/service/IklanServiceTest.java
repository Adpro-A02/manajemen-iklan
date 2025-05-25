package id.ac.ui.cs.advprog.manajemen_iklan.service;

import id.ac.ui.cs.advprog.manajemen_iklan.dto.IklanDTO;
import id.ac.ui.cs.advprog.manajemen_iklan.dto.IklanResponseDTO;
import id.ac.ui.cs.advprog.manajemen_iklan.enums.IklanStatus;
import id.ac.ui.cs.advprog.manajemen_iklan.exception.ResourceNotFoundException;
import id.ac.ui.cs.advprog.manajemen_iklan.mapper.IklanMapper;
import id.ac.ui.cs.advprog.manajemen_iklan.model.IklanModel;
import id.ac.ui.cs.advprog.manajemen_iklan.repository.IklanRepository;
import id.ac.ui.cs.advprog.manajemen_iklan.service.cache.IklanCacheManager;
import id.ac.ui.cs.advprog.manajemen_iklan.service.filter.IklanSpecificationBuilder;
import id.ac.ui.cs.advprog.manajemen_iklan.service.tracking.IklanTrackingService;
import id.ac.ui.cs.advprog.manajemen_iklan.service.validation.IklanValidator;
import id.ac.ui.cs.advprog.manajemen_iklan.utils.ResponseFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IklanServiceTest {

    @Mock
    private IklanRepository iklanRepository;

    @Mock
    private IklanMapper iklanMapper;

    @Mock
    private IklanValidator validator;

    @Mock
    private IklanSpecificationBuilder specBuilder;

    @Mock
    private ResponseFactory responseFactory;

    @Mock
    private IklanCacheManager cacheManager;

    @Mock
    private IklanTrackingService trackingService;

    @InjectMocks
    private IklanServiceImpl iklanService;

    private IklanModel testIklan;
    private IklanDTO testIklanDTO;
    private IklanResponseDTO testResponse;

    @BeforeEach
    void setUp() {
        testIklan = IklanModel.builder()
                .id("test-id")
                .title("Test Advertisement")
                .description("Test Description")
                .imageUrl("https://example.com/image.jpg")
                .clickUrl("https://example.com")
                .position("banner")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30))
                .status(IklanStatus.ACTIVE)
                .impressions(100)
                .clicks(10)
                .build();

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
                .impressions(100)
                .clicks(10)
                .build();

        testResponse = IklanResponseDTO.builder()
                .code(HttpStatus.OK.value())
                .success(true)
                .message("Success")
                .data(testIklanDTO)
                .build();
    }

    @Test
    void testGetAllAdvertisements() {
        // Given
        when(validator.parseStatus(anyString())).thenReturn(IklanStatus.ACTIVE);
        when(cacheManager.cacheAdvertisements(any(), any(), any(), any(), any(), any(), any(), any(), any(Supplier.class)))
                .thenReturn(testResponse);

        // When
        IklanResponseDTO response = iklanService.getAllAdvertisements(
                1, 10, "active", null, null, null, null, null);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getCode());
        assertTrue(response.isSuccess());
        
        verify(validator).parseStatus("active");
        verify(cacheManager).cacheAdvertisements(any(), any(), any(), any(), any(), any(), any(), any(), any(Supplier.class));
    }

    @Test
    void testGetAdvertisementById_Success() {
        // Given
        when(iklanRepository.findById("test-id")).thenReturn(Optional.of(testIklan));
        when(iklanMapper.mapToDTO(testIklan)).thenReturn(testIklanDTO);
        when(responseFactory.createDetailResponse(testIklanDTO, HttpStatus.OK.value(), "Detail iklan berhasil diambil"))
                .thenReturn(testResponse);

        // When
        IklanResponseDTO response = iklanService.getAdvertisementById("test-id");

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getCode());
        assertTrue(response.isSuccess());
        
        verify(validator).validateId("test-id");
        verify(iklanRepository).findById("test-id");
        verify(iklanMapper).mapToDTO(testIklan);
    }

    @Test
    void testGetAdvertisementById_NotFound() {
        // Given
        when(iklanRepository.findById("non-existent-id")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () ->
                iklanService.getAdvertisementById("non-existent-id"));
        
        verify(validator).validateId("non-existent-id");
        verify(iklanRepository).findById("non-existent-id");
    }

    @Test
    void testCreateAdvertisement() {
        // Given
        when(iklanMapper.createModelFromDTO(testIklanDTO)).thenReturn(testIklan);
        when(iklanRepository.save(testIklan)).thenReturn(testIklan);
        when(iklanMapper.mapToDTO(testIklan)).thenReturn(testIklanDTO);
        when(responseFactory.createDetailResponse(testIklanDTO, HttpStatus.CREATED.value(), "Iklan berhasil ditambahkan"))
                .thenReturn(testResponse);

        // When
        IklanResponseDTO response = iklanService.createAdvertisement(testIklanDTO);

        // Then
        assertNotNull(response);
        verify(validator).validateAdvertisementDTO(testIklanDTO);
        verify(iklanMapper).createModelFromDTO(testIklanDTO);
        verify(iklanRepository).save(testIklan);
        verify(cacheManager).evictAllCaches();
    }
}