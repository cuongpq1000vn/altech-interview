package org.altech.interview.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateDealRequest {
    
    @NotBlank(message = "Deal name is required")
    @Size(min = 1, max = 255, message = "Deal name must be between 1 and 255 characters")
    private String name;
    
    @NotBlank(message = "Deal description is required")
    @Size(min = 1, max = 1000, message = "Deal description must be between 1 and 1000 characters")
    private String description;
    
    @NotNull(message = "Product ID is required")
    @Min(value = 1, message = "Product ID must be positive")
    private Long productId;
    
    @NotNull(message = "Buy quantity is required")
    @Min(value = 1, message = "Buy quantity must be at least 1")
    private Integer buyQuantity;
    
    @NotNull(message = "Get quantity is required")
    @Min(value = 1, message = "Get quantity must be at least 1")
    private Integer getQuantity;
    
    @NotNull(message = "Discount percentage is required")
    @DecimalMin(value = "0.01", message = "Discount percentage must be greater than 0")
    @DecimalMax(value = "100.00", message = "Discount percentage cannot exceed 100%")
    @Digits(integer = 3, fraction = 2, message = "Discount percentage must have at most 3 digits before decimal and 2 after")
    private BigDecimal discountPercentage;
    
    @NotNull(message = "Expiration date is required")
    @Future(message = "Expiration date must be in the future")
    private LocalDateTime expiresAt;
} 