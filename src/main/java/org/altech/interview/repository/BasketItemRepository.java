package org.altech.interview.repository;

import java.util.List;
import java.util.Optional;

import org.altech.interview.entity.BasketItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BasketItemRepository extends JpaRepository<BasketItem, Long> {
    
    List<BasketItem> findByBasketId(Long basketId);
    
    Optional<BasketItem> findByBasketIdAndProductId(Long basketId, Long productId);
    
    void deleteByBasketIdAndProductId(Long basketId, Long productId);
    
    void deleteByBasketId(Long basketId);
} 