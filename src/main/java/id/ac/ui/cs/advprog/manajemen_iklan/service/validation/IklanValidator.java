package id.ac.ui.cs.advprog.manajemen_iklan.service.validation;

import id.ac.ui.cs.advprog.manajemen_iklan.dto.IklanDTO;
import id.ac.ui.cs.advprog.manajemen_iklan.enums.IklanStatus;
import id.ac.ui.cs.advprog.manajemen_iklan.exception.InvalidStatusException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class IklanValidator {

    public void validateId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID iklan tidak boleh kosong");
        }
    }
    
    public void validateStatus(IklanStatus status) {
        if (status == null) {
            throw new InvalidStatusException("Status iklan tidak boleh kosong");
        }
        
        if (status != IklanStatus.ACTIVE && status != IklanStatus.INACTIVE) {
            throw new InvalidStatusException("Status harus berupa 'active' atau 'inactive'");
        }
    }
    
    public void validateAdvertisementDTO(IklanDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Data iklan tidak boleh kosong");
        }
        validateTitle(dto.getTitle());
        validateImageUrl(dto.getImageUrl());
        validateDescription(dto.getDescription());
        validatePosition(dto.getPosition());
        validateDates(dto.getStartDate(), dto.getEndDate(), dto.getId() == null); 
        
    }
    
    private void validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Judul iklan tidak boleh kosong");
        }
        
        if (title.length() > 100) {
            throw new IllegalArgumentException("Judul iklan tidak boleh lebih dari 100 karakter");
        }
    }
    
    private void validateImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("URL gambar iklan tidak boleh kosong");
        }
    }
    
    private void validateDates(LocalDateTime startDate, LocalDateTime endDate, boolean isNewAd) {
        if (startDate == null) {
            throw new IllegalArgumentException("Tanggal mulai iklan tidak boleh kosong");
        }
        
        if (endDate == null) {
            throw new IllegalArgumentException("Tanggal selesai iklan tidak boleh kosong");
        }
        
        if (!endDate.isAfter(startDate)) {
            throw new IllegalArgumentException("Tanggal selesai harus setelah tanggal mulai");
        }
    }

    private void validateDescription(String description) {
        if (description != null && description.length() > 500) {
            throw new IllegalArgumentException("Deskripsi iklan tidak boleh lebih dari 500 karakter");
        }
    }
    
    private void validatePosition(String position) {
        if (position != null && 
            !position.equals("homepage_top") && 
            !position.equals("homepage_middle") && 
            !position.equals("homepage_bottom")) {
            throw new IllegalArgumentException("Posisi harus berupa 'homepage_top', 'homepage_middle', atau 'homepage_bottom'");
        }
    }
    
    public IklanStatus parseStatus(String statusParam) {
        if (statusParam == null || statusParam.isEmpty()) {
            return null;
        }
        
        try {
            switch(statusParam.toLowerCase()) {
            case "active":
                return IklanStatus.ACTIVE;
            case "inactive":
                return IklanStatus.INACTIVE;
            case "expired":
                return IklanStatus.EXPIRED;
            default:
                return null;
            }
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}