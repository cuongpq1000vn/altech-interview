package org.altech.interview.controller;

import java.util.List;

import org.altech.interview.dto.*;
import org.altech.interview.service.BasketService;
import org.altech.interview.service.DealService;
import org.altech.interview.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerController {
    
    private final ProductService productService;
    private final DealService dealService;
    private final BasketService basketService;
    
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
    
    @GetMapping("/products/category/{category}")
    public ResponseEntity<Page<ProductDto>> getProductsByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<ProductDto> products = productService.getProductsByCategory(category, page, size);
        return ResponseEntity.ok(products);
    }
    
    @PostMapping("/products/filter")
    public ResponseEntity<Page<ProductDto>> filterProducts(@RequestBody ProductFilterRequest filterRequest) {
        Page<ProductDto> products = productService.filterProducts(filterRequest);
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/products/{productId}/deals")
    public ResponseEntity<List<DealDto>> getActiveDealsForProduct(@PathVariable Long productId) {
        List<DealDto> deals = dealService.getActiveDealsForProduct(productId);
        return ResponseEntity.ok(deals);
    }
    
    @PostMapping("/basket/items")
    public ResponseEntity<BasketDto> addItemToBasket(
            @RequestParam String customerId,
            @Valid @RequestBody AddToBasketRequest request) {
        BasketDto basket = basketService.addItemToBasket(customerId, request);
        return ResponseEntity.ok(basket);
    }
    
    @DeleteMapping("/basket/items/{productId}")
    public ResponseEntity<BasketDto> removeItemFromBasket(
            @RequestParam String customerId,
            @PathVariable Long productId) {
        BasketDto basket = basketService.removeItemFromBasket(customerId, productId);
        return ResponseEntity.ok(basket);
    }
    
    @GetMapping("/basket")
    public ResponseEntity<BasketDto> getBasket(@RequestParam String customerId) {
        BasketDto basket = basketService.getBasket(customerId);
        return ResponseEntity.ok(basket);
    }
    
    @GetMapping("/basket/receipt")
    public ResponseEntity<BasketDto> calculateReceipt(@RequestParam String customerId) {
        BasketDto receipt = basketService.calculateReceipt(customerId);
        return ResponseEntity.ok(receipt);
    }
    
    @DeleteMapping("/basket")
    public ResponseEntity<Void> clearBasket(@RequestParam String customerId) {
        basketService.clearBasket(customerId);
        return ResponseEntity.noContent().build();
    }
} 