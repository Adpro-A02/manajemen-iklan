package id.ac.ui.cs.advprog.manajemen_iklan.service.cache;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class IklanCacheManager {
    
    @Cacheable(value = "advertisementsCache", 
               key = "{#page, #limit, #status, #startDateFrom, #startDateTo, #endDateFrom, #endDateTo, #search}",
               condition = "#search == null || #search.length() > 3")
    public <T> T cacheAdvertisements(
            Integer page,
            Integer limit,
            String status,
            Object startDateFrom,
            Object startDateTo,
            Object endDateFrom,
            Object endDateTo,
            String search,
            java.util.function.Supplier<T> dataSupplier) {
        
        return dataSupplier.get();
    }
    
    @Cacheable(value = "publicAdvertisementsCache", key = "{#position, #limit}")
    public <T> T cachePublicAdvertisements(
            String position,
            Integer limit,
            java.util.function.Supplier<T> dataSupplier) {
        
        return dataSupplier.get();
    }
    
    @CacheEvict(value = {"advertisementsCache", "publicAdvertisementsCache"}, allEntries = true)
    public void evictAllCaches() {
        // This method doesn't need to do anything - just trigger the annotation
    }
}