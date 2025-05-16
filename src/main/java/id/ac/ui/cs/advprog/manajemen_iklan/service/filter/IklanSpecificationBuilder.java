package id.ac.ui.cs.advprog.manajemen_iklan.service.filter;

import id.ac.ui.cs.advprog.manajemen_iklan.enums.IklanStatus;
import id.ac.ui.cs.advprog.manajemen_iklan.model.IklanModel;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class IklanSpecificationBuilder {

    public Specification<IklanModel> buildFilterSpecification(
            IklanStatus status,
            LocalDateTime startDateFrom,
            LocalDateTime startDateTo,
            LocalDateTime endDateFrom,
            LocalDateTime endDateTo,
            String search) {
        
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Filter by status
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }
            
            // Filter by start date range
            if (startDateFrom != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("startDate"), startDateFrom));
            }
            
            if (startDateTo != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("startDate"), startDateTo));
            }
            
            // Filter by end date range
            if (endDateFrom != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("endDate"), endDateFrom));
            }
            
            if (endDateTo != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("endDate"), endDateTo));
            }
            
            // Search by title or description
            if (search != null && !search.trim().isEmpty()) {
                String likePattern = "%" + search.toLowerCase() + "%";
                Predicate titlePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), likePattern);
                Predicate descriptionPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), likePattern);
                predicates.add(criteriaBuilder.or(titlePredicate, descriptionPredicate));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
    
    public Specification<IklanModel> buildPublicAdSpecification(String position) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Only active ads
            predicates.add(criteriaBuilder.equal(root.get("status"), IklanStatus.ACTIVE));
            
            // Current date must be between start and end dates
            LocalDateTime now = LocalDateTime.now();
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("startDate"), now));
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("endDate"), now));
            
            // Filter by position if provided
            if (position != null && !position.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("position"), position));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}