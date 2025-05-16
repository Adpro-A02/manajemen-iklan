package id.ac.ui.cs.advprog.manajemen_iklan.service.tracking;

import id.ac.ui.cs.advprog.manajemen_iklan.model.IklanModel;
import id.ac.ui.cs.advprog.manajemen_iklan.repository.IklanRepository;
import id.ac.ui.cs.advprog.manajemen_iklan.service.validation.IklanValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class IklanTrackingService {
    private final IklanRepository iklanRepository;
    private final IklanValidator validator;
    
    @Transactional
    public void incrementImpressions(String id) {
        validator.validateId(id);
        IklanModel iklan = iklanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Iklan dengan id " + id + " tidak ditemukan"));
        iklan.setImpressions(iklan.getImpressions() + 1);
        iklanRepository.save(iklan);
    }
    
    @Transactional
    public void incrementClicks(String id) {
        validator.validateId(id);
        IklanModel iklan = iklanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Iklan dengan id " + id + " tidak ditemukan"));
        iklan.setClicks(iklan.getClicks() + 1);
        iklanRepository.save(iklan);
    }
}