package org.altech.interview.service;

import org.altech.interview.dto.CreateProductRequest;
import org.altech.interview.dto.ProductDto;
import org.altech.interview.dto.ProductFilterRequest;
import org.springframework.data.domain.Page;

public interface ProductService {
    
    ProductDto createProduct(CreateProductRequest request);
    
    ProductDto getProductById(Long id);
    
    Page<ProductDto> getAllProducts(int page, int size);
    
    Page<ProductDto> getProductsByCategory(String category, int page, int size);
    
    Page<ProductDto> filterProducts(ProductFilterRequest filterRequest);
    
    void deleteProduct(Long id);
    
    ProductDto updateProductStock(Long id, Integer newStockQuantity);
} 