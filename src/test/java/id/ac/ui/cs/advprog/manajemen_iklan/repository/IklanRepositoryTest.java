package id.ac.ui.cs.advprog.manajemen_iklan.repository;

import id.ac.ui.cs.advprog.manajemen_iklan.enums.IklanStatus;
import id.ac.ui.cs.advprog.manajemen_iklan.model.IklanModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})

class IklanRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private IklanRepository iklanRepository;

    private IklanModel testIklan;

    @BeforeEach
    void setUp() {
        testIklan = IklanModel.builder()
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
    }

    @Test
    void testSaveAndFindById() {
        IklanModel savedIklan = iklanRepository.save(testIklan);
        
        assertNotNull(savedIklan.getId());
        
        Optional<IklanModel> foundIklan = iklanRepository.findById(savedIklan.getId());
        assertTrue(foundIklan.isPresent());
        assertEquals(testIklan.getTitle(), foundIklan.get().getTitle());
        assertEquals(testIklan.getDescription(), foundIklan.get().getDescription());
    }

    @Test
    void testFindAll() {
        iklanRepository.save(testIklan);
        
        IklanModel secondIklan = IklanModel.builder()
                .title("Second Advertisement")
                .description("Second Description")
                .imageUrl("https://example.com/image2.jpg")
                .clickUrl("https://example.com/2")
                .position("sidebar")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(15))
                .status(IklanStatus.INACTIVE)
                .build();
        iklanRepository.save(secondIklan);

        List<IklanModel> allIklans = iklanRepository.findAll();
        assertEquals(2, allIklans.size());
    }

    @Test
    void testFindWithPagination() {
        // Save multiple test records
        for (int i = 0; i < 15; i++) {
            IklanModel iklan = IklanModel.builder()
                    .title("Test Ad " + i)
                    .description("Description " + i)
                    .imageUrl("https://example.com/image" + i + ".jpg")
                    .clickUrl("https://example.com/" + i)
                    .position("banner")
                    .startDate(LocalDateTime.now())
                    .endDate(LocalDateTime.now().plusDays(30))
                    .status(IklanStatus.ACTIVE)
                    .build();
            iklanRepository.save(iklan);
        }

        Pageable pageable = PageRequest.of(0, 10);
        Page<IklanModel> page = iklanRepository.findAll(pageable);

        assertEquals(10, page.getContent().size());
        assertEquals(15, page.getTotalElements());
        assertEquals(2, page.getTotalPages());
    }

    @Test
    void testDeleteById() {
        IklanModel savedIklan = iklanRepository.save(testIklan);
        String iklanId = savedIklan.getId();

        iklanRepository.deleteById(iklanId);

        Optional<IklanModel> deletedIklan = iklanRepository.findById(iklanId);
        assertFalse(deletedIklan.isPresent());
    }

    @Test
    void testFindByStatus() {
        iklanRepository.save(testIklan);
        
        IklanModel inactiveIklan = IklanModel.builder()
                .title("Inactive Ad")
                .description("Inactive Description")
                .imageUrl("https://example.com/inactive.jpg")
                .clickUrl("https://example.com/inactive")
                .position("banner")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30))
                .status(IklanStatus.INACTIVE)
                .build();
        iklanRepository.save(inactiveIklan);

        Specification<IklanModel> spec = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), IklanStatus.ACTIVE);

        List<IklanModel> activeAds = iklanRepository.findAll(spec);
        assertEquals(1, activeAds.size());
        assertEquals(IklanStatus.ACTIVE, activeAds.get(0).getStatus());
    }

    @Test
    void testUpdateImpressions() {
        IklanModel savedIklan = iklanRepository.save(testIklan);
        
        savedIklan.setImpressions(savedIklan.getImpressions() + 50);
        iklanRepository.save(savedIklan);

        Optional<IklanModel> updatedIklan = iklanRepository.findById(savedIklan.getId());
        assertTrue(updatedIklan.isPresent());
        assertEquals(150, updatedIklan.get().getImpressions());
    }

    @Test
    void testUpdateClicks() {
        IklanModel savedIklan = iklanRepository.save(testIklan);
        
        savedIklan.setClicks(savedIklan.getClicks() + 5);
        iklanRepository.save(savedIklan);

        Optional<IklanModel> updatedIklan = iklanRepository.findById(savedIklan.getId());
        assertTrue(updatedIklan.isPresent());
        assertEquals(15, updatedIklan.get().getClicks());
    }

    @Test
    void testFindByMultipleCriteria() {
        // Create test data with different criteria
        IklanModel activeAd = IklanModel.builder()
                .title("Active Ad")
                .description("Active Description")
                .imageUrl("https://example.com/active.jpg")
                .clickUrl("https://example.com/active")
                .position("banner")
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().plusDays(30))
                .status(IklanStatus.ACTIVE)
                .build();
        
        IklanModel expiredAd = IklanModel.builder()
                .title("Expired Ad")
                .description("Expired Description")
                .imageUrl("https://example.com/expired.jpg")
                .clickUrl("https://example.com/expired")
                .position("sidebar")
                .startDate(LocalDateTime.now().minusDays(30))
                .endDate(LocalDateTime.now().minusDays(1))
                .status(IklanStatus.EXPIRED)
                .build();
        
        iklanRepository.save(activeAd);
        iklanRepository.save(expiredAd);

        // Test complex specification
        Specification<IklanModel> spec = (root, query, criteriaBuilder) -> {
            return criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("status"), IklanStatus.ACTIVE),
                    criteriaBuilder.equal(root.get("position"), "banner")
            );
        };

        List<IklanModel> results = iklanRepository.findAll(spec);
        assertEquals(1, results.size());
        assertEquals("Active Ad", results.get(0).getTitle());
    }
}