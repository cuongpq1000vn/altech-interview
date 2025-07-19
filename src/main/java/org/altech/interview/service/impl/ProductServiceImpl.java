package org.altech.interview.service.impl;

import org.altech.interview.dto.CreateProductRequest;
import org.altech.interview.dto.ProductDto;
import org.altech.interview.dto.ProductFilterRequest;
import org.altech.interview.entity.Product;
import org.altech.interview.exception.ProductNameAlreadyExistsException;
import org.altech.interview.exception.ResourceNotFoundException;
import org.altech.interview.mapper.product.ProductEntityMapper;
import org.altech.interview.mapper.product.ProductMapper;
import org.altech.interview.repository.ProductRepository;
import org.altech.interview.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {
    
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductEntityMapper productEntityMapper;
    
    @Override
    @Transactional
    public ProductDto createProduct(CreateProductRequest request) {
        if (productRepository.existsByNameAndIsDeleteFalse(request.getName())) {
            throw new ProductNameAlreadyExistsException(request.getName());
        }

        Product product = productEntityMapper.toEntity(request);
        Product savedProduct = productRepository.save(product);
        return productMapper.toDto(savedProduct);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ProductDto getProductById(Long id) {
        Product product = productRepository.findByIdAndIsDeleteFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        return productMapper.toDto(product);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> getAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findByIsDeleteFalse(pageable)
                .map(productMapper::toDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> getProductsByCategory(String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findByCategoryAndIsDeleteFalse(category, pageable)
                .map(productMapper::toDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> filterProducts(ProductFilterRequest filterRequest) {
        Pageable pageable = PageRequest.of(filterRequest.getPage(), filterRequest.getSize());
        return productRepository.findWithFilters(
                filterRequest.getCategory(),
                filterRequest.getMinPrice(),
                filterRequest.getMaxPrice(),
                filterRequest.getInStock(),
                pageable
        ).map(productMapper::toDto);
    }
    
    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findByIdAndIsDeleteFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        product.setIsDelete(true);
        productRepository.save(product);
    }
    
    @Override
    @Transactional
    public ProductDto updateProductStock(Long id, Integer newStockQuantity) {
        Product product = productRepository.findByIdAndIsDeleteFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        product.setStockQuantity(newStockQuantity);
        Product savedProduct = productRepository.save(product);
        return productMapper.toDto(savedProduct);
    }
} 