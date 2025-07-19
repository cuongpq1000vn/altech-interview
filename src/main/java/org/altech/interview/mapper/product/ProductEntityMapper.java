package org.altech.interview.mapper.product;

import org.altech.interview.dto.CreateProductRequest;
import org.altech.interview.entity.Product;
import org.altech.interview.mapper.EntityMapper;
import org.springframework.stereotype.Component;

@Component
public class ProductEntityMapper extends EntityMapper<CreateProductRequest, Product> {

    @Override
    public Product toEntity(CreateProductRequest dto) {
        if (dto == null) {
            return null;
        }
        
        return Product.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .category(dto.getCategory())
                .stockQuantity(dto.getStockQuantity())
                .build();
    }
} 