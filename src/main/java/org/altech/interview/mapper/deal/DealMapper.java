package org.altech.interview.mapper.deal;

import org.altech.interview.dto.DealDto;
import org.altech.interview.entity.Deal;
import org.altech.interview.mapper.DtoMapper;
import org.springframework.stereotype.Component;

@Component
public class DealMapper extends DtoMapper<Deal, DealDto> {

    @Override
    public DealDto toDto(Deal entity) {
        if (entity == null) {
            return null;
        }
        
        return DealDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .productId(entity.getProduct() != null ? entity.getProduct().getId() : null)
                .productName(entity.getProduct() != null ? entity.getProduct().getName() : null)
                .buyQuantity(entity.getBuyQuantity())
                .getQuantity(entity.getGetQuantity())
                .discountPercentage(entity.getDiscountPercentage())
                .expiresAt(entity.getExpiresAt())
                .createdAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime() : null)
                .updatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime() : null)
                .build();
    }
} 