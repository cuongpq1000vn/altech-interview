package org.altech.interview.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductFilterRequest {
    private String category;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Boolean inStock;
    private int page = 0;
    private int size = 20;
} 