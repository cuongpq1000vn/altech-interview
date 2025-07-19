package org.altech.interview.mapper.basket;

import org.altech.interview.dto.BasketDto;
import org.altech.interview.entity.Basket;
import org.altech.interview.mapper.DtoMapper;
import org.springframework.stereotype.Component;

@Component
public class BasketMapper extends DtoMapper<Basket, BasketDto> {

    @Override
    public BasketDto toDto(Basket entity) {
        if (entity == null) {
            return null;
        }
        
        return BasketDto.builder()
                .id(entity.getId())
                .customerId(entity.getCustomerId())
                .createdAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime() : null)
                .updatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime() : null)
                .build();
    }
} 