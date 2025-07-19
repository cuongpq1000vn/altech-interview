package org.altech.interview.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DealDto {
    private Long id;
    private String name;
    private String description;
    private Long productId;
    private String productName;
    private Integer buyQuantity;
    private Integer getQuantity;
    private BigDecimal discountPercentage;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 