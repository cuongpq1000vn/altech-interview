package org.altech.interview.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.altech.interview.dto.CreateDealRequest;
import org.altech.interview.dto.DealDto;
import org.altech.interview.entity.Deal;
import org.altech.interview.entity.Product;
import org.altech.interview.exception.ResourceNotFoundException;
import org.altech.interview.mapper.deal.DealEntityMapper;
import org.altech.interview.mapper.deal.DealMapper;
import org.altech.interview.repository.DealRepository;
import org.altech.interview.repository.ProductRepository;
import org.altech.interview.service.DealService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DealServiceImpl implements DealService {
    
    private final DealRepository dealRepository;
    private final ProductRepository productRepository;
    private final DealMapper dealMapper;
    private final DealEntityMapper dealEntityMapper;
    
    @Override
    @Transactional
    public DealDto createDeal(CreateDealRequest request) {
        Product product = productRepository.findByIdAndIsDeleteFalse(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", request.getProductId()));
        
        Deal deal = dealEntityMapper.toEntity(request);
        deal.setProduct(product);
        Deal savedDeal = dealRepository.save(deal);
        return dealMapper.toDto(savedDeal);
    }
    
    @Override
    @Transactional(readOnly = true)
    public DealDto getDealById(Long id) {
        Deal deal = dealRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deal", "id", id));
        return dealMapper.toDto(deal);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<DealDto> getAllDeals(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return dealRepository.findByIsDeleteFalse(pageable)
                .map(dealMapper::toDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<DealDto> getActiveDealsForProduct(Long productId) {
        return dealRepository.findActiveDealsForProduct(productId, LocalDateTime.now())
                .stream()
                .map(dealMapper::toDto)
                .toList();
    }
    
    @Override
    @Transactional
    public void deleteDeal(Long id) {
        Deal deal = dealRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deal", "id", id));
        deal.setIsDelete(true);
        dealRepository.save(deal);
    }
    
    @Override
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void expireDeals() {
        List<Deal> expiredDeals = dealRepository.findByExpiresAtBeforeAndIsDeleteFalse(LocalDateTime.now());
        if (!expiredDeals.isEmpty()) {
            expiredDeals.forEach(deal -> deal.setIsDelete(true));
            dealRepository.saveAll(expiredDeals);
        }
    }
} 