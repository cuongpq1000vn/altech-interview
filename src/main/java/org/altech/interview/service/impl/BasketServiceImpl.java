package org.altech.interview.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.altech.interview.dto.AddToBasketRequest;
import org.altech.interview.dto.BasketDto;
import org.altech.interview.dto.BasketItemDto;
import org.altech.interview.entity.Basket;
import org.altech.interview.entity.BasketItem;
import org.altech.interview.entity.Deal;
import org.altech.interview.entity.Product;
import org.altech.interview.exception.InsufficientStockException;
import org.altech.interview.exception.ResourceNotFoundException;
import org.altech.interview.mapper.basket.BasketItemMapper;
import org.altech.interview.mapper.basket.BasketMapper;
import org.altech.interview.repository.BasketItemRepository;
import org.altech.interview.repository.BasketRepository;
import org.altech.interview.repository.DealRepository;
import org.altech.interview.repository.ProductRepository;
import org.altech.interview.service.BasketService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BasketServiceImpl implements BasketService {
    
    private final BasketRepository basketRepository;
    private final BasketItemRepository basketItemRepository;
    private final ProductRepository productRepository;
    private final DealRepository dealRepository;
    private final BasketMapper basketMapper;
    private final BasketItemMapper basketItemMapper;
    
    @Override
    public BasketDto addItemToBasket(String customerId, AddToBasketRequest request) {
        Product product = productRepository.findByIdAndIsDeleteFalse(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", request.getProductId()));
        
        if (product.getStockQuantity() < request.getQuantity()) {
            throw new InsufficientStockException(product.getName(), request.getQuantity(), product.getStockQuantity());
        }
        
        Basket basket = getOrCreateBasket(customerId);
        
        Optional<BasketItem> existingItem = basketItemRepository.findByBasketIdAndProductId(basket.getId(), product.getId());
        
        if (existingItem.isPresent()) {
            BasketItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
            basketItemRepository.save(item);
        } else {
            BasketItem newItem = new BasketItem();
            newItem.setBasket(basket);
            newItem.setProduct(product);
            newItem.setQuantity(request.getQuantity());
            newItem.setUnitPrice(product.getPrice());
            basketItemRepository.save(newItem);
        }
        
        product.setStockQuantity(product.getStockQuantity() - request.getQuantity());
        productRepository.save(product);
        
        return calculateReceipt(customerId);
    }
    
    @Override
    public BasketDto removeItemFromBasket(String customerId, Long productId) {
        Basket basket = getOrCreateBasket(customerId);
        basketItemRepository.deleteByBasketIdAndProductId(basket.getId(), productId);
        return calculateReceipt(customerId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public BasketDto getBasket(String customerId) {
        Basket basket = getOrCreateBasket(customerId);
        return mapToDto(basket);
    }
    
    @Override
    @Transactional(readOnly = true)
    public BasketDto calculateReceipt(String customerId) {
        Basket basket = getOrCreateBasket(customerId);
        return mapToDto(basket);
    }
    
    @Override
    public void clearBasket(String customerId) {
        Basket basket = getOrCreateBasket(customerId);
        basketItemRepository.deleteByBasketId(basket.getId());
    }
    
    private Basket getOrCreateBasket(String customerId) {
        return basketRepository.findByCustomerId(customerId)
                .orElseGet(() -> {
                    Basket newBasket = new Basket();
                    newBasket.setCustomerId(customerId);
                    return basketRepository.save(newBasket);
                });
    }
    
    private BasketDto mapToDto(Basket basket) {
        List<BasketItem> items = basketItemRepository.findByBasketId(basket.getId());
        List<BasketItemDto> itemDtos = items.stream()
                .map(this::mapItemToDto)
                .toList();
        
        BigDecimal totalPrice = itemDtos.stream()
                .map(BasketItemDto::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalDiscount = itemDtos.stream()
                .map(BasketItemDto::getDiscountApplied)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal finalPrice = totalPrice.subtract(totalDiscount);
        
        BasketDto basketDto = basketMapper.toDto(basket);
        basketDto.setItems(itemDtos);
        basketDto.setTotalPrice(totalPrice);
        basketDto.setTotalDiscount(totalDiscount);
        basketDto.setFinalPrice(finalPrice);
        
        return basketDto;
    }
    
    private BasketItemDto mapItemToDto(BasketItem item) {
        BigDecimal totalPrice = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
        BigDecimal discountApplied = calculateDiscount(item);
        BigDecimal finalPrice = totalPrice.subtract(discountApplied);
        
        BasketItemDto itemDto = basketItemMapper.toDto(item);
        itemDto.setDiscountApplied(discountApplied);
        itemDto.setFinalPrice(finalPrice);
        
        return itemDto;
    }
    
    private BigDecimal calculateDiscount(BasketItem item) {
        List<Deal> activeDeals = dealRepository.findActiveDealsForProduct(item.getProduct().getId(), LocalDateTime.now());
        
        BigDecimal totalDiscount = BigDecimal.ZERO;
        for (Deal deal : activeDeals) {
            if (item.getQuantity() >= deal.getBuyQuantity()) {
                int dealGroups = item.getQuantity() / deal.getBuyQuantity();
                int discountedItems = dealGroups * deal.getGetQuantity();
                BigDecimal discountPerItem = item.getUnitPrice().multiply(deal.getDiscountPercentage()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                totalDiscount = totalDiscount.add(discountPerItem.multiply(BigDecimal.valueOf(discountedItems)));
            }
        }
        
        return totalDiscount;
    }
} 