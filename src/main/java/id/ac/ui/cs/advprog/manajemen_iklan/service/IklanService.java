package id.ac.ui.cs.advprog.manajemen_iklan.service;

import id.ac.ui.cs.advprog.manajemen_iklan.dto.IklanDTO;
import id.ac.ui.cs.advprog.manajemen_iklan.dto.IklanResponseDTO;
import id.ac.ui.cs.advprog.manajemen_iklan.enums.IklanStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

// Service for managing advertisements
public interface IklanService {
    IklanResponseDTO getAllAdvertisements(
            Integer page,
            Integer limit,
            String status,
            LocalDateTime startDateFrom,
            LocalDateTime startDateTo,
            LocalDateTime endDateFrom,
            LocalDateTime endDateTo,
            String search);
    
    IklanResponseDTO getAdvertisementById(String id);
    IklanResponseDTO createAdvertisement(IklanDTO iklanDTO);
    IklanResponseDTO updateAdvertisement(String id, IklanDTO iklanDTO);
    IklanResponseDTO updateAdvertisementStatus(String id, IklanStatus status);
    IklanResponseDTO deleteAdvertisement(String id);
    IklanResponseDTO getPublicAdvertisements(String position, Integer limit);
    CompletableFuture<IklanResponseDTO> updateAdvertisementStatusAsync(String id, IklanStatus status);
    CompletableFuture<IklanResponseDTO> batchUpdateStatusAsync(List<String> ids, IklanStatus status);
    CompletableFuture<IklanResponseDTO> generateAdvertisementReportAsync(LocalDateTime startDate, LocalDateTime endDate);
}