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
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IklanServiceImpl implements IklanService {
    private static final Logger logger = LoggerFactory.getLogger(IklanServiceImpl.class);
    
    private final IklanRepository iklanRepository;
    private final IklanMapper iklanMapper;
    private final IklanValidator validator;
    private final IklanSpecificationBuilder specBuilder;
    private final ResponseFactory responseFactory;
    private final IklanCacheManager cacheManager;
    private final IklanTrackingService trackingService;
    
    @Override
    public IklanResponseDTO getAllAdvertisements(
            Integer page,
            Integer limit,
            String statusParam,
            LocalDateTime startDateFrom,
            LocalDateTime startDateTo,
            LocalDateTime endDateFrom,
            LocalDateTime endDateTo,
            String search) {
        
        logger.debug("Fetching advertisements with filters: status={}, search={}, page={}, limit={}", 
                statusParam, search, page, limit);
        
        int pageNumber = (page == null || page < 1) ? 0 : page - 1;
        int pageSize = (limit == null || limit < 1) ? 10 : Math.min(limit, 50);
        IklanStatus status = validator.parseStatus(statusParam);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        return cacheManager.cacheAdvertisements(
            page, limit, statusParam, startDateFrom, startDateTo, endDateFrom, endDateTo, search,
            () -> {
                Specification<IklanModel> spec = specBuilder.buildFilterSpecification(
                    status, startDateFrom, startDateTo, endDateFrom, endDateTo, search);
                Page<IklanModel> advertisementsPage = iklanRepository.findAll(spec, pageable);
                Page<IklanDTO> dtoPage = new PageImpl<>(
                    iklanMapper.mapToDTOList(advertisementsPage.getContent()),
                    pageable,
                    advertisementsPage.getTotalElements()
                );
                logger.debug("Found {} advertisements matching the criteria", advertisementsPage.getTotalElements());
                return responseFactory.createListResponse(
                    dtoPage, pageNumber + 1, pageSize, "Daftar iklan berhasil diambil");
            }
        );
    }
    
    @Override
    public IklanResponseDTO getAdvertisementById(String id) {
        logger.debug("Fetching advertisement with id: {}", id);
        validator.validateId(id);
        IklanModel iklan = findIklanById(id);
        IklanDTO iklanDTO = iklanMapper.mapToDTO(iklan);
        return responseFactory.createDetailResponse(
            iklanDTO, HttpStatus.OK.value(), "Detail iklan berhasil diambil"
        );
    }
    
    @Override
    @Transactional
    public IklanResponseDTO createAdvertisement(IklanDTO iklanDTO) {
        logger.debug("Creating new advertisement: {}", iklanDTO.getTitle());
        validator.validateAdvertisementDTO(iklanDTO);
        
        IklanModel iklan = iklanMapper.createModelFromDTO(iklanDTO);
        IklanModel savedIklan = iklanRepository.save(iklan);
        
        cacheManager.evictAllCaches();
        return responseFactory.createDetailResponse(
            iklanMapper.mapToDTO(savedIklan),
            HttpStatus.CREATED.value(),
            "Iklan berhasil ditambahkan"
        );
    }
    
    @Override
    @Transactional
    public IklanResponseDTO updateAdvertisement(String id, IklanDTO iklanDTO) {
        logger.debug("Updating advertisement with id: {}", id);
        validator.validateId(id);
        validator.validateAdvertisementDTO(iklanDTO);
        
        IklanModel existingIklan = findIklanById(id);
        iklanMapper.updateModelFromDTO(existingIklan, iklanDTO);
        IklanModel updatedIklan = iklanRepository.save(existingIklan);
        
        cacheManager.evictAllCaches();
        
        return responseFactory.createDetailResponse(
            iklanMapper.mapToDTO(updatedIklan),
            HttpStatus.OK.value(),
            "Iklan berhasil diperbarui"
        );
    }
    
    @Override
    @Transactional
    public IklanResponseDTO updateAdvertisementStatus(String id, IklanStatus status) {
        logger.debug("Updating status of advertisement with id: {} to {}", id, status);
        validator.validateId(id);
        validator.validateStatus(status);
        
        IklanModel existingIklan = findIklanById(id);    
        existingIklan.setStatus(status);
        IklanModel updatedIklan = iklanRepository.save(existingIklan);
        
        cacheManager.evictAllCaches();
        
        return responseFactory.createStatusUpdateResponse(
            updatedIklan.getId(),
            updatedIklan.getStatus().toString().toLowerCase(),
            updatedIklan.getUpdatedAt()
        );
    }
    
    @Override
    @Transactional
    public IklanResponseDTO deleteAdvertisement(String id) {
        logger.debug("Deleting advertisement with id: {}", id);
        
        validator.validateId(id);
        IklanModel existingIklan = findIklanById(id);
        iklanRepository.delete(existingIklan);
        
        cacheManager.evictAllCaches();
        
        return responseFactory.createDeleteResponse();
    }
    
    @Override
    public IklanResponseDTO getPublicAdvertisements(String position, Integer limit) {
        logger.debug("Fetching public advertisements with position: {}, limit: {}", position, limit);
        
        int maxResults = (limit == null || limit < 1) ? 3 : limit;
        
        return cacheManager.cachePublicAdvertisements(position, limit, () -> {
            Specification<IklanModel> spec = specBuilder.buildPublicAdSpecification(position);
            Pageable pageable = PageRequest.of(0, maxResults, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<IklanModel> advertisementsPage = iklanRepository.findAll(spec, pageable);
            
            logger.debug("Found {} public advertisements", advertisementsPage.getNumberOfElements());

            List<IklanDTO> publicAdDTOs = iklanMapper.mapToPublicDTOList(advertisementsPage.getContent());

            return responseFactory.createPublicListResponse(
                publicAdDTOs, "Daftar iklan berhasil diambil");
        });
    }
    

// helper methods
    public void incrementImpressions(String id) {
        trackingService.incrementImpressions(id);
    }
    
    public void incrementClicks(String id) {
        trackingService.incrementClicks(id);
    }
    
    private IklanModel findIklanById(String id) {
        return iklanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Iklan dengan id " + id + " tidak ditemukan"));
    }
}