package org.altech.interview.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.altech.interview.dto.CreateProductRequest;
import org.altech.interview.dto.ProductDto;
import org.altech.interview.dto.ProductFilterRequest;
import org.altech.interview.entity.Product;
import org.altech.interview.exception.ResourceNotFoundException;
import org.altech.interview.mapper.product.ProductEntityMapper;
import org.altech.interview.mapper.product.ProductMapper;
import org.altech.interview.repository.ProductRepository;
import org.altech.interview.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private ProductEntityMapper productEntityMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product testProduct;
    private ProductDto testProductDto;
    private CreateProductRequest createProductRequest;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setPrice(new BigDecimal(99.99));
        testProduct.setCategory("Electronics");
        testProduct.setStockQuantity(10);
        testProduct.setIsDelete(false);

        testProductDto = new ProductDto();
        testProductDto.setId(1L);
        testProductDto.setName("Test Product");
        testProductDto.setDescription("Test Description");
        testProductDto.setPrice(new BigDecimal(99.99));
        testProductDto.setCategory("Electronics");
        testProductDto.setStockQuantity(10);
        testProductDto.setCreatedAt(LocalDateTime.now());
        testProductDto.setUpdatedAt(LocalDateTime.now());

        createProductRequest = new CreateProductRequest();
        createProductRequest.setName("Test Product");
        createProductRequest.setDescription("Test Description");
        createProductRequest.setPrice(new BigDecimal(99.99));
        createProductRequest.setCategory("Electronics");
        createProductRequest.setStockQuantity(10);
    }

    @Test
    void createProduct_Success() {
        when(productEntityMapper.toEntity(createProductRequest)).thenReturn(testProduct);
        when(productRepository.save(testProduct)).thenReturn(testProduct);
        when(productMapper.toDto(testProduct)).thenReturn(testProductDto);

        ProductDto result = productService.createProduct(createProductRequest);

        assertNotNull(result);
        assertEquals(testProductDto.getId(), result.getId());
        assertEquals(testProductDto.getName(), result.getName());
        verify(productEntityMapper).toEntity(createProductRequest);
        verify(productRepository).save(testProduct);
        verify(productMapper).toDto(testProduct);
    }

    @Test
    void getProductById_Success() {
        Long productId = 1L;
        when(productRepository.findByIdAndIsDeleteFalse(productId)).thenReturn(Optional.of(testProduct));
        when(productMapper.toDto(testProduct)).thenReturn(testProductDto);

        ProductDto result = productService.getProductById(productId);

        assertNotNull(result);
        assertEquals(testProductDto.getId(), result.getId());
        verify(productRepository).findByIdAndIsDeleteFalse(productId);
        verify(productMapper).toDto(testProduct);
    }

    @Test
    void getProductById_NotFound() {
        Long productId = 999L;
        when(productRepository.findByIdAndIsDeleteFalse(productId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(productId));
        verify(productRepository).findByIdAndIsDeleteFalse(productId);
        verify(productMapper, never()).toDto(any());
    }

    @Test
    void getAllProducts_Success() {
        int page = 0;
        int size = 20;
        Pageable pageable = PageRequest.of(page, size);
        List<Product> products = Arrays.asList(testProduct);
        Page<Product> productPage = new PageImpl<>(products, pageable, 1);
        when(productRepository.findByIsDeleteFalse(pageable)).thenReturn(productPage);
        when(productMapper.toDto(testProduct)).thenReturn(testProductDto);

        Page<ProductDto> result = productService.getAllProducts(page, size);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testProductDto, result.getContent().get(0));
        verify(productRepository).findByIsDeleteFalse(pageable);
    }

    @Test
    void getProductsByCategory_Success() {
        String category = "Electronics";
        int page = 0;
        int size = 20;
        Pageable pageable = PageRequest.of(page, size);
        List<Product> products = Arrays.asList(testProduct);
        Page<Product> productPage = new PageImpl<>(products, pageable, 1);
        when(productRepository.findByCategoryAndIsDeleteFalse(category, pageable)).thenReturn(productPage);
        when(productMapper.toDto(testProduct)).thenReturn(testProductDto);

        Page<ProductDto> result = productService.getProductsByCategory(category, page, size);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testProductDto, result.getContent().get(0));
        verify(productRepository).findByCategoryAndIsDeleteFalse(category, pageable);
    }

    @Test
    void filterProducts_Success() {
        ProductFilterRequest filterRequest = new ProductFilterRequest();
        filterRequest.setCategory("Electronics");
        filterRequest.setMinPrice(new BigDecimal("50.00"));
        filterRequest.setMaxPrice(new BigDecimal("150.00"));
        filterRequest.setInStock(true);
        filterRequest.setPage(0);
        filterRequest.setSize(20);

        Pageable pageable = PageRequest.of(filterRequest.getPage(), filterRequest.getSize());
        List<Product> products = Arrays.asList(testProduct);
        Page<Product> productPage = new PageImpl<>(products, pageable, 1);
        when(productRepository.findWithFilters(
                filterRequest.getCategory(),
                filterRequest.getMinPrice(),
                filterRequest.getMaxPrice(),
                filterRequest.getInStock(),
                pageable
        )).thenReturn(productPage);
        when(productMapper.toDto(testProduct)).thenReturn(testProductDto);

        Page<ProductDto> result = productService.filterProducts(filterRequest);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testProductDto, result.getContent().get(0));
        verify(productRepository).findWithFilters(
                filterRequest.getCategory(),
                filterRequest.getMinPrice(),
                filterRequest.getMaxPrice(),
                filterRequest.getInStock(),
                pageable
        );
    }

    @Test
    void deleteProduct_Success() {
        Long productId = 1L;
        when(productRepository.findByIdAndIsDeleteFalse(productId)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        productService.deleteProduct(productId);

        verify(productRepository).findByIdAndIsDeleteFalse(productId);
        verify(productRepository).save(any(Product.class));
        assertTrue(testProduct.getIsDelete());
    }

    @Test
    void deleteProduct_NotFound() {
        Long productId = 999L;
        when(productRepository.findByIdAndIsDeleteFalse(productId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(productId));
        verify(productRepository).findByIdAndIsDeleteFalse(productId);
        verify(productRepository, never()).save(any());
    }

    @Test
    void updateProductStock_Success() {
        Long productId = 1L;
        Integer newStockQuantity = 25;
        Product updatedProduct = new Product();
        updatedProduct.setId(1L);
        updatedProduct.setName("Test Product");
        updatedProduct.setDescription("Test Description");
        updatedProduct.setPrice(new BigDecimal(99.99));
        updatedProduct.setCategory("Electronics");
        updatedProduct.setStockQuantity(newStockQuantity);
        updatedProduct.setIsDelete(false);

        when(productRepository.findByIdAndIsDeleteFalse(productId)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);
        when(productMapper.toDto(updatedProduct)).thenReturn(testProductDto);

        ProductDto result = productService.updateProductStock(productId, newStockQuantity);

        assertNotNull(result);
        verify(productRepository).findByIdAndIsDeleteFalse(productId);
        verify(productRepository).save(any(Product.class));
        verify(productMapper).toDto(updatedProduct);
    }

    @Test
    void updateProductStock_NotFound() {
        Long productId = 999L;
        Integer newStockQuantity = 25;
        when(productRepository.findByIdAndIsDeleteFalse(productId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.updateProductStock(productId, newStockQuantity));
        verify(productRepository).findByIdAndIsDeleteFalse(productId);
        verify(productRepository, never()).save(any());
    }
} 