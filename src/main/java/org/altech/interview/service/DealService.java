package org.altech.interview.service;

import java.util.List;

import org.altech.interview.dto.CreateDealRequest;
import org.altech.interview.dto.DealDto;
import org.springframework.data.domain.Page;

public interface DealService {
    
    DealDto createDeal(CreateDealRequest request);
    
    DealDto getDealById(Long id);
    
    Page<DealDto> getAllDeals(int page, int size);
    
    List<DealDto> getActiveDealsForProduct(Long productId);
    
    void deleteDeal(Long id);
    
    void expireDeals();
} 