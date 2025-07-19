package org.altech.interview.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.altech.interview.entity.Deal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DealRepository extends JpaRepository<Deal, Long> {
    
    Page<Deal> findByIsDeleteFalse(Pageable pageable);
    
    @Query("SELECT d FROM Deal d WHERE d.isDelete = false " +
           "AND d.product.id = :productId " +
           "AND d.expiresAt > :currentTime")
    List<Deal> findActiveDealsForProduct(@Param("productId") Long productId, @Param("currentTime") LocalDateTime currentTime);
    
    List<Deal> findByExpiresAtBeforeAndIsDeleteFalse(LocalDateTime currentTime);
} 