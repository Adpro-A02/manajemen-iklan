package id.ac.ui.cs.advprog.manajemen_iklan.service.tracking;

import id.ac.ui.cs.advprog.manajemen_iklan.model.IklanModel;
import id.ac.ui.cs.advprog.manajemen_iklan.repository.IklanRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class IklanTrackingService {
    
    private static final Logger logger = LoggerFactory.getLogger(IklanTrackingService.class);
    private final IklanRepository iklanRepository;

    @Async("trackingExecutor")
    @Transactional
    public CompletableFuture<Void> incrementImpressions(String id) {
        logger.debug("Incrementing impressions for ad ID: {}", id);
        try {
            iklanRepository.findById(id).ifPresent(iklan -> {
                iklan.setImpressions(iklan.getImpressions() + 1);
                iklanRepository.save(iklan);
                logger.debug("Incremented impressions for ad ID: {} to {}", id, iklan.getImpressions());
            });
        } catch (Exception e) {
            logger.error("Failed to increment impressions for ad ID {}: {}", id, e.getMessage(), e);
        }
        return CompletableFuture.completedFuture(null);
    }
    
    @Async("trackingExecutor")
    @Transactional
    public CompletableFuture<Void> incrementClicks(String id) {
        logger.debug("Incrementing clicks for ad ID: {}", id);
        try {
            iklanRepository.findById(id).ifPresent(iklan -> {
                iklan.setClicks(iklan.getClicks() + 1);
                iklanRepository.save(iklan);
                logger.debug("Incremented clicks for ad ID: {} to {}", id, iklan.getClicks());
            });
        } catch (Exception e) {
            logger.error("Failed to increment clicks for ad ID {}: {}", id, e.getMessage(), e);
        }
        return CompletableFuture.completedFuture(null);
    }
    
    @Async("trackingExecutor")
    @Transactional
    public CompletableFuture<Void> batchUpdateImpressions(IklanModel iklan, int count) {
        iklan.setImpressions(iklan.getImpressions() + count);
        iklanRepository.save(iklan);
        return CompletableFuture.completedFuture(null);
    }
}