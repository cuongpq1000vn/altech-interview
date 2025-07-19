package org.altech.interview.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import org.altech.interview.dto.AddToBasketRequest;
import org.altech.interview.dto.BasketDto;
import org.altech.interview.dto.BasketItemDto;
import org.altech.interview.entity.Basket;
import org.altech.interview.entity.BasketItem;
import org.altech.interview.entity.Deal;
import org.altech.interview.entity.Product;
import org.altech.interview.exception.InsufficientStockException;
import org.altech.interview.exception.ResourceNotFoundException;
import org.altech.interview.mapper.basket.BasketItemMapper;
import org.altech.interview.mapper.basket.BasketMapper;
import org.altech.interview.repository.BasketItemRepository;
import org.altech.interview.repository.BasketRepository;
import org.altech.interview.repository.DealRepository;
import org.altech.interview.repository.ProductRepository;
import org.altech.interview.service.impl.BasketServiceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BasketServiceTest {

    @Mock
    private BasketRepository basketRepository;

    @Mock
    private BasketItemRepository basketItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private DealRepository dealRepository;

    @Mock
    private BasketMapper basketMapper;

    @Mock
    private BasketItemMapper basketItemMapper;

    @InjectMocks
    private BasketServiceImpl basketService;

    private Basket testBasket;
    private BasketDto testBasketDto;
    private BasketItem testBasketItem;
    private BasketItemDto testBasketItemDto;
    private Product testProduct;
    private Deal testDeal;
    private AddToBasketRequest addToBasketRequest;

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

        testDeal = new Deal();
        testDeal.setId(1L);
        testDeal.setName("Test Deal");
        testDeal.setDescription("Test Deal Description");
        testDeal.setProduct(testProduct);
        testDeal.setBuyQuantity(2);
        testDeal.setGetQuantity(3);
        testDeal.setDiscountPercentage(new BigDecimal(10));
        testDeal.setExpiresAt(LocalDateTime.now().plusDays(7));
        testDeal.setIsDelete(false);

        testBasket = new Basket();
        testBasket.setId(1L);
        testBasket.setCustomerId("customer123");
        testBasketItem = new BasketItem();
        testBasketItem.setId(1L);
        testBasketItem.setBasket(testBasket);
        testBasketItem.setProduct(testProduct);
        testBasketItem.setQuantity(2);
        testBasketItem.setUnitPrice(new BigDecimal(99.99));

        testBasketItemDto = new BasketItemDto();
        testBasketItemDto.setId(1L);
        testBasketItemDto.setProductId(1L);
        testBasketItemDto.setProductName("Test Product");
        testBasketItemDto.setQuantity(2);
        testBasketItemDto.setUnitPrice(new BigDecimal(99.99));
        testBasketItemDto.setTotalPrice(new BigDecimal(199.98));
        testBasketItemDto.setDiscountApplied(BigDecimal.ZERO);
        testBasketItemDto.setFinalPrice(new BigDecimal(199.98));

        testBasketDto = new BasketDto();
        testBasketDto.setId(1L);
        testBasketDto.setCustomerId("customer123");
        testBasketDto.setItems(Arrays.asList(testBasketItemDto));
        testBasketDto.setTotalPrice(new BigDecimal(199.98));
        testBasketDto.setTotalDiscount(BigDecimal.ZERO);
        testBasketDto.setFinalPrice(new BigDecimal(199.98));

        addToBasketRequest = new AddToBasketRequest();
        addToBasketRequest.setProductId(1L);
        addToBasketRequest.setQuantity(2);
    }

    @Test
    void addItemToBasket_Success_NewItem() {
        String customerId = "customer123";
        when(productRepository.findByIdAndIsDeleteFalse(addToBasketRequest.getProductId())).thenReturn(Optional.of(testProduct));
        when(basketRepository.findByCustomerId(customerId)).thenReturn(Optional.of(testBasket));
        when(basketItemRepository.findByBasketIdAndProductId(eq(testBasket.getId()), eq(testProduct.getId()))).thenReturn(Optional.empty());
        when(basketItemRepository.save(any(BasketItem.class))).thenReturn(testBasketItem);
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        when(basketItemRepository.findByBasketId(testBasket.getId())).thenReturn(Arrays.asList(testBasketItem));
        when(basketItemMapper.toDto(testBasketItem)).thenReturn(testBasketItemDto);
        when(basketMapper.toDto(testBasket)).thenReturn(testBasketDto);
        when(dealRepository.findActiveDealsForProduct(eq(testProduct.getId()), any(LocalDateTime.class))).thenReturn(Arrays.asList());

        BasketDto result = basketService.addItemToBasket(customerId, addToBasketRequest);

        assertNotNull(result);
        assertEquals(testBasketDto.getId(), result.getId());
        verify(productRepository).findByIdAndIsDeleteFalse(addToBasketRequest.getProductId());
        verify(basketRepository, times(2)).findByCustomerId(customerId);
        verify(basketItemRepository).save(any(BasketItem.class));
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void addItemToBasket_Success_ExistingItem() {
        String customerId = "customer123";
        when(productRepository.findByIdAndIsDeleteFalse(addToBasketRequest.getProductId())).thenReturn(Optional.of(testProduct));
        when(basketRepository.findByCustomerId(customerId)).thenReturn(Optional.of(testBasket));
        when(basketItemRepository.findByBasketIdAndProductId(eq(testBasket.getId()), eq(testProduct.getId()))).thenReturn(Optional.of(testBasketItem));
        when(basketItemRepository.save(any(BasketItem.class))).thenReturn(testBasketItem);
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        when(basketItemRepository.findByBasketId(testBasket.getId())).thenReturn(Arrays.asList(testBasketItem));
        when(basketItemMapper.toDto(testBasketItem)).thenReturn(testBasketItemDto);
        when(basketMapper.toDto(testBasket)).thenReturn(testBasketDto);
        when(dealRepository.findActiveDealsForProduct(eq(testProduct.getId()), any(LocalDateTime.class))).thenReturn(Arrays.asList());

        BasketDto result = basketService.addItemToBasket(customerId, addToBasketRequest);

        assertNotNull(result);
        assertEquals(testBasketDto.getId(), result.getId());
        verify(productRepository).findByIdAndIsDeleteFalse(addToBasketRequest.getProductId());
        verify(basketRepository, times(2)).findByCustomerId(customerId);
        verify(basketItemRepository).save(any(BasketItem.class));
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void addItemToBasket_BasketNotFound_CreatesNewBasket() {
        String customerId = "customer123";
        when(productRepository.findByIdAndIsDeleteFalse(addToBasketRequest.getProductId())).thenReturn(Optional.of(testProduct));
        when(basketRepository.findByCustomerId(customerId)).thenReturn(Optional.empty());
        when(basketRepository.save(any(Basket.class))).thenReturn(testBasket);
        when(basketItemRepository.findByBasketIdAndProductId(eq(testBasket.getId()), eq(testProduct.getId()))).thenReturn(Optional.empty());
        when(basketItemRepository.save(any(BasketItem.class))).thenReturn(testBasketItem);
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        when(basketItemRepository.findByBasketId(testBasket.getId())).thenReturn(Arrays.asList(testBasketItem));
        when(basketItemMapper.toDto(testBasketItem)).thenReturn(testBasketItemDto);
        when(basketMapper.toDto(testBasket)).thenReturn(testBasketDto);
        when(dealRepository.findActiveDealsForProduct(eq(testProduct.getId()), any(LocalDateTime.class))).thenReturn(Arrays.asList());

        BasketDto result = basketService.addItemToBasket(customerId, addToBasketRequest);

        assertNotNull(result);
        assertEquals(testBasketDto.getId(), result.getId());
        verify(basketRepository, atLeastOnce()).findByCustomerId(customerId);
        verify(basketRepository, atLeastOnce()).save(any(Basket.class));
    }

    @Test
    void addItemToBasket_ProductNotFound() {
        String customerId = "customer123";
        when(productRepository.findByIdAndIsDeleteFalse(addToBasketRequest.getProductId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> basketService.addItemToBasket(customerId, addToBasketRequest));
        verify(productRepository).findByIdAndIsDeleteFalse(addToBasketRequest.getProductId());
        verify(basketItemRepository, never()).save(any());
    }

    @Test
    void addItemToBasket_InsufficientStock() {
        String customerId = "customer123";
        addToBasketRequest.setQuantity(15);
        when(productRepository.findByIdAndIsDeleteFalse(addToBasketRequest.getProductId())).thenReturn(Optional.of(testProduct));

        assertThrows(InsufficientStockException.class, () -> basketService.addItemToBasket(customerId, addToBasketRequest));
        verify(productRepository).findByIdAndIsDeleteFalse(addToBasketRequest.getProductId());
        verify(basketItemRepository, never()).save(any());
    }

    @Test
    void removeItemFromBasket_Success() {
        String customerId = "customer123";
        Long productId = 1L;
        when(basketRepository.findByCustomerId(customerId)).thenReturn(Optional.of(testBasket));
        when(basketItemRepository.findByBasketId(testBasket.getId())).thenReturn(Arrays.asList());
        when(basketMapper.toDto(testBasket)).thenReturn(testBasketDto);

        BasketDto result = basketService.removeItemFromBasket(customerId, productId);

        assertNotNull(result);
        assertEquals(testBasketDto.getId(), result.getId());
        verify(basketRepository, atLeastOnce()).findByCustomerId(customerId);
        verify(basketItemRepository).deleteByBasketIdAndProductId(testBasket.getId(), productId);
    }

    @Test
    void getBasket_Success() {
        String customerId = "customer123";
        when(basketRepository.findByCustomerId(customerId)).thenReturn(Optional.of(testBasket));
        when(basketItemRepository.findByBasketId(testBasket.getId())).thenReturn(Arrays.asList());
        when(basketMapper.toDto(testBasket)).thenReturn(testBasketDto);

        BasketDto result = basketService.getBasket(customerId);

        assertNotNull(result);
        assertEquals(testBasketDto.getId(), result.getId());
        assertEquals(testBasketDto.getCustomerId(), result.getCustomerId());
        verify(basketRepository).findByCustomerId(customerId);
    }

    @Test
    void calculateReceipt_Success() {
        String customerId = "customer123";
        when(basketRepository.findByCustomerId(customerId)).thenReturn(Optional.of(testBasket));
        when(basketItemRepository.findByBasketId(testBasket.getId())).thenReturn(Arrays.asList(testBasketItem));
        when(basketItemMapper.toDto(testBasketItem)).thenReturn(testBasketItemDto);
        when(basketMapper.toDto(testBasket)).thenReturn(testBasketDto);
        when(dealRepository.findActiveDealsForProduct(eq(testProduct.getId()), any(LocalDateTime.class))).thenReturn(Arrays.asList());

        BasketDto result = basketService.calculateReceipt(customerId);

        assertNotNull(result);
        assertEquals(testBasketDto.getId(), result.getId());
        assertEquals(testBasketDto.getTotalPrice(), result.getTotalPrice());
        verify(basketRepository).findByCustomerId(customerId);
    }

    @Test
    void clearBasket_Success() {
        String customerId = "customer123";
        when(basketRepository.findByCustomerId(customerId)).thenReturn(Optional.of(testBasket));

        basketService.clearBasket(customerId);

        verify(basketRepository).findByCustomerId(customerId);
        verify(basketItemRepository).deleteByBasketId(testBasket.getId());
    }
} 