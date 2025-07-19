package org.altech.interview.repository;

import java.util.Optional;

import org.altech.interview.entity.Basket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BasketRepository extends JpaRepository<Basket, Long> {
    
    Optional<Basket> findByCustomerId(String customerId);
} 