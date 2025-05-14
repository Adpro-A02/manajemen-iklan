package id.ac.ui.cs.advprog.manajemen_iklan.repository;

import id.ac.ui.cs.advprog.manajemen_iklan.enums.IklanStatus;
import id.ac.ui.cs.advprog.manajemen_iklan.model.IklanModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface IklanRepository extends JpaRepository<IklanModel, String>, JpaSpecificationExecutor<IklanModel> {
    
    @Query("SELECT i FROM IklanModel i WHERE " +
           "(:status IS NULL OR i.status = :status) AND " +
           "(:startDateFrom IS NULL OR i.startDate >= :startDateFrom) AND " +
           "(:startDateTo IS NULL OR i.startDate <= :startDateTo) AND " +
           "(:endDateFrom IS NULL OR i.endDate >= :endDateFrom) AND " +
           "(:endDateTo IS NULL OR i.endDate <= :endDateTo) AND " +
           "(:search IS NULL OR LOWER(i.title) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<IklanModel> findAllWithFilters(
            @Param("status") IklanStatus status,
            @Param("startDateFrom") LocalDateTime startDateFrom,
            @Param("startDateTo") LocalDateTime startDateTo,
            @Param("endDateFrom") LocalDateTime endDateFrom,
            @Param("endDateTo") LocalDateTime endDateTo,
            @Param("search") String search,
            Pageable pageable);
}