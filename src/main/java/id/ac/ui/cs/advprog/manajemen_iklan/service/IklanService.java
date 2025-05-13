package id.ac.ui.cs.advprog.manajemen_iklan.service;
import id.ac.ui.cs.advprog.manajemen_iklan.dto.IklanResponseDTO;
import id.ac.ui.cs.advprog.manajemen_iklan.enums.IklanStatus;

import java.time.LocalDateTime;

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
}