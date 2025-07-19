package org.altech.interview.controller;

import org.altech.interview.dto.CreateDealRequest;
import org.altech.interview.dto.CreateProductRequest;
import org.altech.interview.dto.DealDto;
import org.altech.interview.dto.ProductDto;
import org.altech.interview.service.DealService;
import org.altech.interview.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    
    private final ProductService productService;
    private final DealService dealService;
    
    @PostMapping("/products")
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody CreateProductRequest request) {
        ProductDto product = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }
    
    @GetMapping("/products")
    public ResponseEntity<Page<ProductDto>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<ProductDto> products = productService.getAllProducts(page, size);
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/products/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        ProductDto product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }
    
    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/products/{id}/stock")
    public ResponseEntity<ProductDto> updateProductStock(
            @PathVariable Long id,
            @RequestParam Integer stockQuantity) {
        ProductDto product = productService.updateProductStock(id, stockQuantity);
        return ResponseEntity.ok(product);
    }
    
    @PostMapping("/deals")
    public ResponseEntity<DealDto> createDeal(@Valid @RequestBody CreateDealRequest request) {
        DealDto deal = dealService.createDeal(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(deal);
    }
    
    @GetMapping("/deals")
    public ResponseEntity<Page<DealDto>> getAllDeals(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<DealDto> deals = dealService.getAllDeals(page, size);
        return ResponseEntity.ok(deals);
    }
    
    @GetMapping("/deals/{id}")
    public ResponseEntity<DealDto> getDealById(@PathVariable Long id) {
        DealDto deal = dealService.getDealById(id);
        return ResponseEntity.ok(deal);
    }
    
    @DeleteMapping("/deals/{id}")
    public ResponseEntity<Void> deleteDeal(@PathVariable Long id) {
        dealService.deleteDeal(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/deals/deactivate-expired")
    public ResponseEntity<Void> deactivateExpiredDeals() {
        dealService.expireDeals();
        return ResponseEntity.ok().build();
    }
} 