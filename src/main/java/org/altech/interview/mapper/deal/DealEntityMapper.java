package org.altech.interview.mapper.deal;

import org.altech.interview.dto.CreateDealRequest;
import org.altech.interview.entity.Deal;
import org.altech.interview.mapper.EntityMapper;
import org.springframework.stereotype.Component;

@Component
public class DealEntityMapper extends EntityMapper<CreateDealRequest, Deal> {

    @Override
    public Deal toEntity(CreateDealRequest dto) {
        if (dto == null) {
            return null;
        }
        
        return Deal.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .buyQuantity(dto.getBuyQuantity())
                .getQuantity(dto.getGetQuantity())
                .discountPercentage(dto.getDiscountPercentage())
                .expiresAt(dto.getExpiresAt())
                .build();
    }
} 