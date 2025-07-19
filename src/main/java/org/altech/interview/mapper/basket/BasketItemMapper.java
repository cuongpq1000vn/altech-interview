package org.altech.interview.mapper.basket;

import org.altech.interview.dto.BasketItemDto;
import org.altech.interview.entity.BasketItem;
import org.altech.interview.mapper.DtoMapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class BasketItemMapper extends DtoMapper<BasketItem, BasketItemDto> {

    @Override
    public BasketItemDto toDto(BasketItem entity) {
        if (entity == null) {
            return null;
        }
        
        BigDecimal totalPrice = entity.getUnitPrice().multiply(BigDecimal.valueOf(entity.getQuantity()));
        
        return BasketItemDto.builder()
                .id(entity.getId())
                .productId(entity.getProduct() != null ? entity.getProduct().getId() : null)
                .productName(entity.getProduct() != null ? entity.getProduct().getName() : null)
                .productDescription(entity.getProduct() != null ? entity.getProduct().getDescription() : null)
                .quantity(entity.getQuantity())
                .unitPrice(entity.getUnitPrice())
                .totalPrice(totalPrice)
                .build();
    }
} 