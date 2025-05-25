package id.ac.ui.cs.advprog.manajemen_iklan.mapper;
import id.ac.ui.cs.advprog.manajemen_iklan.dto.IklanDTO;
import id.ac.ui.cs.advprog.manajemen_iklan.enums.IklanStatus;
import id.ac.ui.cs.advprog.manajemen_iklan.model.IklanModel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class IklanMapper {
    
    public IklanDTO mapToDTO(IklanModel model) {
        return IklanDTO.builder()
            .id(model.getId())
            .title(model.getTitle())
            .description(model.getDescription())
            .imageUrl(model.getImageUrl())
            .startDate(model.getStartDate())
            .endDate(model.getEndDate())
            .status(model.getStatus())
            .clickUrl(model.getClickUrl())
            .position(model.getPosition())
            .impressions(model.getImpressions())
            .clicks(model.getClicks())
            .createdAt(model.getCreatedAt())
            .updatedAt(model.getUpdatedAt())
            .build();
    }
    
    public List<IklanDTO> mapToDTOList(List<IklanModel> models) {
        return models.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public IklanDTO mapToPublicDTO(IklanModel model) {
        return IklanDTO.builder()
                .id(model.getId())
                .title(model.getTitle())
                .imageUrl(model.getImageUrl())
                .clickUrl(model.getClickUrl())
                .build();
    }
    
    public List<IklanDTO> mapToPublicDTOList(List<IklanModel> models) {
        return models.stream()
                .map(this::mapToPublicDTO)
                .collect(Collectors.toList());
    }
    
    public IklanModel createModelFromDTO(IklanDTO dto) {
        return IklanModel.builder()
            .title(dto.getTitle())
            .description(dto.getDescription())
            .imageUrl(dto.getImageUrl())
            .startDate(dto.getStartDate())
            .endDate(dto.getEndDate())
            .status(dto.getStatus() != null ? dto.getStatus() : IklanStatus.INACTIVE)
            .clickUrl(dto.getClickUrl())
            .position(dto.getPosition())
            .impressions(dto.getImpressions() != null ? dto.getImpressions() : 0)
            .clicks(dto.getClicks() != null ? dto.getClicks() : 0)
            .build();
    }
    
    public void updateModelFromDTO(IklanModel iklan, IklanDTO dto) {
        iklan.setTitle(dto.getTitle());
        iklan.setDescription(dto.getDescription());
        iklan.setImageUrl(dto.getImageUrl());
        iklan.setStartDate(dto.getStartDate());
        iklan.setEndDate(dto.getEndDate());
        iklan.setStatus(dto.getStatus());
        iklan.setClickUrl(dto.getClickUrl());
        iklan.setPosition(dto.getPosition());
    }
}