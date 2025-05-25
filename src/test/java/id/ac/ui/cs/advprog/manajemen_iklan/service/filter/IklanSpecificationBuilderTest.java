package id.ac.ui.cs.advprog.manajemen_iklan.service.filter;

import id.ac.ui.cs.advprog.manajemen_iklan.enums.IklanStatus;
import id.ac.ui.cs.advprog.manajemen_iklan.model.IklanModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class IklanSpecificationBuilderTest {

    private IklanSpecificationBuilder specificationBuilder;

    @BeforeEach
    void setUp() {
        specificationBuilder = new IklanSpecificationBuilder();
    }

    @Test
    void testBuildFilterSpecification_WithStatus() {
        // When
        Specification<IklanModel> spec = specificationBuilder.buildFilterSpecification(
                IklanStatus.ACTIVE, null, null, null, null, null);

        // Then
        assertNotNull(spec);
    }

    @Test
    void testBuildFilterSpecification_WithDateRange() {
        // Given
        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        LocalDateTime endDate = LocalDateTime.now();

        // When
        Specification<IklanModel> spec = specificationBuilder.buildFilterSpecification(
                null, startDate, endDate, null, null, null);

        // Then
        assertNotNull(spec);
    }

    @Test
    void testBuildFilterSpecification_WithSearch() {
        // When
        Specification<IklanModel> spec = specificationBuilder.buildFilterSpecification(
                null, null, null, null, null, "test search");

        // Then
        assertNotNull(spec);
    }

    @Test
    void testBuildFilterSpecification_WithAllParameters() {
        // Given
        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        LocalDateTime endDate = LocalDateTime.now();

        // When
        Specification<IklanModel> spec = specificationBuilder.buildFilterSpecification(
                IklanStatus.ACTIVE, startDate, endDate, startDate, endDate, "test");

        // Then
        assertNotNull(spec);
    }

    @Test
    void testBuildFilterSpecification_WithNullParameters() {
        // When
        Specification<IklanModel> spec = specificationBuilder.buildFilterSpecification(
                null, null, null, null, null, null);

        // Then
        assertNotNull(spec);
    }

    @Test
    void testBuildFilterSpecification_WithEmptySearch() {
        // When
        Specification<IklanModel> spec = specificationBuilder.buildFilterSpecification(
                null, null, null, null, null, "");

        // Then
        assertNotNull(spec);
    }

    @Test
    void testBuildFilterSpecification_WithWhitespaceSearch() {
        // When
        Specification<IklanModel> spec = specificationBuilder.buildFilterSpecification(
                null, null, null, null, null, "   ");

        // Then
        assertNotNull(spec);
    }

    @Test
    void testBuildPublicAdSpecification_WithPosition() {
        // When
        Specification<IklanModel> spec = specificationBuilder.buildPublicAdSpecification("banner");

        // Then
        assertNotNull(spec);
    }

    @Test
    void testBuildPublicAdSpecification_WithoutPosition() {
        // When
        Specification<IklanModel> spec = specificationBuilder.buildPublicAdSpecification(null);

        // Then
        assertNotNull(spec);
    }

    @Test
    void testBuildPublicAdSpecification_WithEmptyPosition() {
        // When
        Specification<IklanModel> spec = specificationBuilder.buildPublicAdSpecification("");

        // Then
        assertNotNull(spec);
    }

    @Test
    void testBuildPublicAdSpecification_WithValidPositions() {
        // Test with different valid positions
        String[] validPositions = {"homepage_top", "homepage_middle", "homepage_bottom", "banner", "sidebar"};
        
        for (String position : validPositions) {
            Specification<IklanModel> spec = specificationBuilder.buildPublicAdSpecification(position);
            assertNotNull(spec, "Specification should not be null for position: " + position);
        }
    }

    @Test
    void testBuildDateRangeSpecification() {
        // Given
        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        LocalDateTime endDate = LocalDateTime.now();

        // When
        Specification<IklanModel> spec = specificationBuilder.buildDateRangeSpecification(startDate, endDate);

        // Then
        assertNotNull(spec);
    }

    @Test
    void testBuildDateRangeSpecification_WithNullDates() {
        // When
        Specification<IklanModel> spec = specificationBuilder.buildDateRangeSpecification(null, null);

        // Then
        assertNotNull(spec);
    }

    @Test
    void testBuildDateRangeSpecification_WithStartDateOnly() {
        // Given
        LocalDateTime startDate = LocalDateTime.now().minusDays(30);

        // When
        Specification<IklanModel> spec = specificationBuilder.buildDateRangeSpecification(startDate, null);

        // Then
        assertNotNull(spec);
    }

    @Test
    void testBuildDateRangeSpecification_WithEndDateOnly() {
        // Given
        LocalDateTime endDate = LocalDateTime.now();

        // When
        Specification<IklanModel> spec = specificationBuilder.buildDateRangeSpecification(null, endDate);

        // Then
        assertNotNull(spec);
    }

    @Test
    void testBuildDateRangeSpecification_WithSameDates() {
        // Given
        LocalDateTime sameDate = LocalDateTime.now();

        // When
        Specification<IklanModel> spec = specificationBuilder.buildDateRangeSpecification(sameDate, sameDate);

        // Then
        assertNotNull(spec);
    }

    @Test
    void testBuildFilterSpecification_ComplexScenario() {
        // Given - Complex scenario with multiple filters
        LocalDateTime startDateFrom = LocalDateTime.now().minusDays(60);
        LocalDateTime startDateTo = LocalDateTime.now().minusDays(30);
        LocalDateTime endDateFrom = LocalDateTime.now().plusDays(30);
        LocalDateTime endDateTo = LocalDateTime.now().plusDays(60);
        String searchTerm = "promotional campaign";

        // When
        Specification<IklanModel> spec = specificationBuilder.buildFilterSpecification(
                IklanStatus.ACTIVE, startDateFrom, startDateTo, endDateFrom, endDateTo, searchTerm);

        // Then
        assertNotNull(spec);
    }
}