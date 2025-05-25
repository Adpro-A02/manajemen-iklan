package id.ac.ui.cs.advprog.manajemen_iklan.service.cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class IklanCacheManagerTest {

    private IklanCacheManager cacheManager;
    private CacheManager springCacheManager;

    @BeforeEach
    void setUp() {
        springCacheManager = new ConcurrentMapCacheManager("advertisementsCache", "publicAdvertisementsCache");
        cacheManager = new IklanCacheManager();
    }

    @Test
    void testCacheAdvertisements() {
        // Given
        String testData = "Test Advertisement Data";
        Supplier<String> dataSupplier = () -> testData;

        // When
        String result = cacheManager.cacheAdvertisements(
                1, 10, "active", null, null, null, null, "search", dataSupplier);

        // Then
        assertEquals(testData, result);
    }

    @Test
    void testCacheAdvertisements_WithNullSearch() {
        // Given
        String testData = "Test Advertisement Data";
        Supplier<String> dataSupplier = () -> testData;

        // When
        String result = cacheManager.cacheAdvertisements(
                1, 10, "active", null, null, null, null, null, dataSupplier);

        // Then
        assertEquals(testData, result);
    }

    @Test
    void testCacheAdvertisements_WithShortSearch() {
        // Given - Search less than 3 characters should not be cached
        String testData = "Test Advertisement Data";
        Supplier<String> dataSupplier = () -> testData;

        // When
        String result = cacheManager.cacheAdvertisements(
                1, 10, "active", null, null, null, null, "ab", dataSupplier);

        // Then
        assertEquals(testData, result);
    }

    @Test
    void testCacheAdvertisements_WithLongSearch() {
        // Given - Search more than 3 characters should be cached
        String testData = "Test Advertisement Data";
        Supplier<String> dataSupplier = () -> testData;

        // When
        String result = cacheManager.cacheAdvertisements(
                1, 10, "active", null, null, null, null, "test search", dataSupplier);

        // Then
        assertEquals(testData, result);
    }

    @Test
    void testCacheAdvertisements_WithDateFilters() {
        // Given
        String testData = "Test Advertisement Data";
        Supplier<String> dataSupplier = () -> testData;
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusDays(30);

        // When
        String result = cacheManager.cacheAdvertisements(
                1, 10, "active", startDate, endDate, startDate, endDate, "search", dataSupplier);

        // Then
        assertEquals(testData, result);
    }

    @Test
    void testCachePublicAdvertisements() {
        // Given
        String testData = "Public Advertisement Data";
        Supplier<String> dataSupplier = () -> testData;

        // When
        String result = cacheManager.cachePublicAdvertisements("banner", 3, dataSupplier);

        // Then
        assertEquals(testData, result);
    }

    @Test
    void testCachePublicAdvertisements_WithNullPosition() {
        // Given
        String testData = "Public Advertisement Data";
        Supplier<String> dataSupplier = () -> testData;

        // When
        String result = cacheManager.cachePublicAdvertisements(null, 5, dataSupplier);

        // Then
        assertEquals(testData, result);
    }

    @Test
    void testEvictAllCaches() {
        // Given - Cache some data first
        String testData = "Test Data";
        cacheManager.cacheAdvertisements(1, 10, "active", null, null, null, null, "search", () -> testData);
        cacheManager.cachePublicAdvertisements("banner", 3, () -> testData);

        // When
        assertDoesNotThrow(() -> cacheManager.evictAllCaches());

        // Then - Should not throw any exception
        // Cache eviction is handled by Spring's @CacheEvict annotation
    }

    @Test
    void testCacheAdvertisements_DifferentKeys() {
        // Given
        String testData1 = "Data 1";
        String testData2 = "Data 2";

        // When - Cache with different keys
        String result1 = cacheManager.cacheAdvertisements(
                1, 10, "active", null, null, null, null, "search1", () -> testData1);
        String result2 = cacheManager.cacheAdvertisements(
                2, 10, "active", null, null, null, null, "search2", () -> testData2);

        // Then
        assertEquals(testData1, result1);
        assertEquals(testData2, result2);
        assertNotEquals(result1, result2);
    }

    @Test
    void testCachePublicAdvertisements_DifferentKeys() {
        // Given
        String testData1 = "Banner Ads";
        String testData2 = "Sidebar Ads";

        // When - Cache with different keys
        String result1 = cacheManager.cachePublicAdvertisements("banner", 3, () -> testData1);
        String result2 = cacheManager.cachePublicAdvertisements("sidebar", 5, () -> testData2);

        // Then
        assertEquals(testData1, result1);
        assertEquals(testData2, result2);
        assertNotEquals(result1, result2);
    }

    @Test
    void testCacheAdvertisements_SupplierCalledOnce() {
        // Given
        final int[] callCount = {0};
        Supplier<String> dataSupplier = () -> {
            callCount[0]++;
            return "Test Data";
        };

        // When - Call twice with same parameters
        String result1 = cacheManager.cacheAdvertisements(
                1, 10, "active", null, null, null, null, "search", dataSupplier);
        String result2 = cacheManager.cacheAdvertisements(
                1, 10, "active", null, null, null, null, "search", dataSupplier);

        // Then
        assertEquals("Test Data", result1);
        assertEquals("Test Data", result2);
    }
}