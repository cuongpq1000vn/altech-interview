package org.altech.interview.mapper.product;

import org.altech.interview.dto.ProductDto;
import org.altech.interview.entity.Product;
import org.altech.interview.mapper.DtoMapper;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper extends DtoMapper<Product, ProductDto> {

    @Override
    public ProductDto toDto(Product entity) {
        if (entity == null) {
            return null;
        }
        
        return ProductDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .category(entity.getCategory())
                .stockQuantity(entity.getStockQuantity())
                .createdAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime() : null)
                .updatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime() : null)
                .build();
    }
} 