package id.ac.ui.cs.advprog.manajemen_iklan.service.tracking;

import id.ac.ui.cs.advprog.manajemen_iklan.enums.IklanStatus;
import id.ac.ui.cs.advprog.manajemen_iklan.model.IklanModel;
import id.ac.ui.cs.advprog.manajemen_iklan.repository.IklanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IklanTrackingServiceTest {

    @Mock
    private IklanRepository iklanRepository;

    @InjectMocks
    private IklanTrackingService trackingService;

    private IklanModel testIklan;

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
                .impressions(10)
                .clicks(5)
                .build();
    }

    @Test
    void testIncrementImpressions_Success() throws Exception {
        // Given
        when(iklanRepository.findById("test-id")).thenReturn(Optional.of(testIklan));
        when(iklanRepository.save(any(IklanModel.class))).thenReturn(testIklan);

        // When
        CompletableFuture<Void> future = trackingService.incrementImpressions("test-id");
        future.get(); // Wait for completion

        // Then
        verify(iklanRepository).findById("test-id");
        verify(iklanRepository).save(testIklan);
        assertEquals(11, testIklan.getImpressions());
    }

    @Test
    void testIncrementImpressions_NotFound() throws Exception {
        // Given
        when(iklanRepository.findById("non-existent-id")).thenReturn(Optional.empty());

        // When
        CompletableFuture<Void> future = trackingService.incrementImpressions("non-existent-id");
        future.get(); // Wait for completion

        // Then
        verify(iklanRepository).findById("non-existent-id");
        verify(iklanRepository, never()).save(any());
    }

    @Test
    void testIncrementClicks_Success() throws Exception {
        // Given
        when(iklanRepository.findById("test-id")).thenReturn(Optional.of(testIklan));
        when(iklanRepository.save(any(IklanModel.class))).thenReturn(testIklan);

        // When
        CompletableFuture<Void> future = trackingService.incrementClicks("test-id");
        future.get(); // Wait for completion

        // Then
        verify(iklanRepository).findById("test-id");
        verify(iklanRepository).save(testIklan);
        assertEquals(6, testIklan.getClicks());
    }

    @Test
    void testIncrementClicks_NotFound() throws Exception {
        // Given
        when(iklanRepository.findById("non-existent-id")).thenReturn(Optional.empty());

        // When
        CompletableFuture<Void> future = trackingService.incrementClicks("non-existent-id");
        future.get(); // Wait for completion

        // Then
        verify(iklanRepository).findById("non-existent-id");
        verify(iklanRepository, never()).save(any());
    }

    @Test
    void testBatchUpdateImpressions() throws Exception {
        // Given
        when(iklanRepository.save(any(IklanModel.class))).thenReturn(testIklan);

        // When
        CompletableFuture<Void> future = trackingService.batchUpdateImpressions(testIklan, 100);
        future.get(); // Wait for completion

        // Then
        verify(iklanRepository).save(testIklan);
        assertEquals(110, testIklan.getImpressions());
    }

    @Test
    void testIncrementImpressions_Exception() throws Exception {
        // Given
        when(iklanRepository.findById("test-id")).thenThrow(new RuntimeException("Database error"));

        // When
        CompletableFuture<Void> future = trackingService.incrementImpressions("test-id");
        
        // Then - Should not throw exception, just log error
        assertDoesNotThrow(() -> future.get());
        verify(iklanRepository).findById("test-id");
        verify(iklanRepository, never()).save(any());
    }

    @Test
    void testIncrementClicks_Exception() throws Exception {
        // Given
        when(iklanRepository.findById("test-id")).thenThrow(new RuntimeException("Database error"));

        // When
        CompletableFuture<Void> future = trackingService.incrementClicks("test-id");
        
        // Then - Should not throw exception, just log error
        assertDoesNotThrow(() -> future.get());
        verify(iklanRepository).findById("test-id");
        verify(iklanRepository, never()).save(any());
    }
}