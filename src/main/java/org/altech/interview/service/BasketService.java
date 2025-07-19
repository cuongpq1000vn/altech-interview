package org.altech.interview.service;

import org.altech.interview.dto.AddToBasketRequest;
import org.altech.interview.dto.BasketDto;

public interface BasketService {
    
    BasketDto addItemToBasket(String customerId, AddToBasketRequest request);
    
    BasketDto removeItemFromBasket(String customerId, Long productId);
    
    BasketDto getBasket(String customerId);
    
    BasketDto calculateReceipt(String customerId);
    
    void clearBasket(String customerId);
} 