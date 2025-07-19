package org.altech.interview.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.altech.interview.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    Page<Product> findByIsDeleteFalse(Pageable pageable);
    
    Page<Product> findByCategoryAndIsDeleteFalse(String category, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.isDelete = false " +
           "AND (:category IS NULL OR p.category = :category) " +
           "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
           "AND (:inStock IS NULL OR (:inStock = true AND p.stockQuantity > 0) OR (:inStock = false AND p.stockQuantity <= 0))")
    Page<Product> findWithFilters(
            @Param("category") String category,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("inStock") Boolean inStock,
            Pageable pageable
    );
    
    Optional<Product> findByIdAndIsDeleteFalse(Long id);
    
    List<Product> findByStockQuantityGreaterThanAndIsDeleteFalse(Integer stockQuantity);
    
    boolean existsByNameAndIsDeleteFalse(String name);
} 