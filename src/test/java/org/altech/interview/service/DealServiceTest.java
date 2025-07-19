package org.altech.interview.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.altech.interview.dto.CreateDealRequest;
import org.altech.interview.dto.DealDto;
import org.altech.interview.entity.Deal;
import org.altech.interview.entity.Product;
import org.altech.interview.exception.ResourceNotFoundException;
import org.altech.interview.mapper.deal.DealEntityMapper;
import org.altech.interview.mapper.deal.DealMapper;
import org.altech.interview.repository.DealRepository;
import org.altech.interview.repository.ProductRepository;
import org.altech.interview.service.impl.DealServiceImpl;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
class DealServiceTest {

    @Mock
    private DealRepository dealRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private DealMapper dealMapper;

    @Mock
    private DealEntityMapper dealEntityMapper;

    @InjectMocks
    private DealServiceImpl dealService;

    private Deal testDeal;
    private DealDto testDealDto;
    private CreateDealRequest createDealRequest;
    private Product testProduct;

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
        testDeal.setDiscountPercentage(new BigDecimal(10.0));
        testDeal.setExpiresAt(LocalDateTime.now().plusDays(7));
        testDeal.setIsDelete(false);

        testDealDto = new DealDto();
        testDealDto.setId(1L);
        testDealDto.setName("Test Deal");
        testDealDto.setDescription("Test Deal Description");
        testDealDto.setProductId(1L);
        testDealDto.setBuyQuantity(2);
        testDealDto.setGetQuantity(3);
        testDealDto.setDiscountPercentage(new BigDecimal(10.0));
        testDealDto.setExpiresAt(LocalDateTime.now().plusDays(7));
        testDealDto.setCreatedAt(LocalDateTime.now());
        testDealDto.setUpdatedAt(LocalDateTime.now());

        createDealRequest = new CreateDealRequest();
        createDealRequest.setName("Test Deal");
        createDealRequest.setDescription("Test Deal Description");
        createDealRequest.setProductId(1L);
        createDealRequest.setBuyQuantity(2);
        createDealRequest.setGetQuantity(3);
        createDealRequest.setDiscountPercentage(new BigDecimal(10.00));
        createDealRequest.setExpiresAt(LocalDateTime.now().plusDays(7));
    }

    @Test
    void createDeal_Success() {
        when(productRepository.findByIdAndIsDeleteFalse(createDealRequest.getProductId())).thenReturn(Optional.of(testProduct));
        when(dealEntityMapper.toEntity(createDealRequest)).thenReturn(testDeal);
        when(dealRepository.save(testDeal)).thenReturn(testDeal);
        when(dealMapper.toDto(testDeal)).thenReturn(testDealDto);

        DealDto result = dealService.createDeal(createDealRequest);

        assertNotNull(result);
        assertEquals(testDealDto.getId(), result.getId());
        assertEquals(testDealDto.getName(), result.getName());
        verify(productRepository).findByIdAndIsDeleteFalse(createDealRequest.getProductId());
        verify(dealEntityMapper).toEntity(createDealRequest);
        verify(dealRepository).save(testDeal);
        verify(dealMapper).toDto(testDeal);
    }

    @Test
    void createDeal_ProductNotFound() {
        when(productRepository.findByIdAndIsDeleteFalse(createDealRequest.getProductId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> dealService.createDeal(createDealRequest));
        verify(productRepository).findByIdAndIsDeleteFalse(createDealRequest.getProductId());
        verify(dealRepository, never()).save(any());
    }

    @Test
    void getDealById_Success() {
        Long dealId = 1L;
        when(dealRepository.findById(dealId)).thenReturn(Optional.of(testDeal));
        when(dealMapper.toDto(testDeal)).thenReturn(testDealDto);

        DealDto result = dealService.getDealById(dealId);

        assertNotNull(result);
        assertEquals(testDealDto.getId(), result.getId());
        verify(dealRepository).findById(dealId);
        verify(dealMapper).toDto(testDeal);
    }

    @Test
    void getDealById_NotFound() {
        Long dealId = 999L;
        when(dealRepository.findById(dealId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> dealService.getDealById(dealId));
        verify(dealRepository).findById(dealId);
        verify(dealMapper, never()).toDto(any());
    }

    @Test
    void getAllDeals_Success() {
        int page = 0;
        int size = 20;
        Pageable pageable = PageRequest.of(page, size);
        List<Deal> deals = Arrays.asList(testDeal);
        Page<Deal> dealPage = new PageImpl<>(deals, pageable,1);
        when(dealRepository.findByIsDeleteFalse(pageable)).thenReturn(dealPage);
        when(dealMapper.toDto(testDeal)).thenReturn(testDealDto);

        Page<DealDto> result = dealService.getAllDeals(page, size);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testDealDto, result.getContent().get(0));
        verify(dealRepository).findByIsDeleteFalse(pageable);
    }

    @Test
    void getActiveDealsForProduct_Success() {
        Long productId =1L;
        List<Deal> deals = Arrays.asList(testDeal);
        when(dealRepository.findActiveDealsForProduct(eq(productId), any(LocalDateTime.class))).thenReturn(deals);
        when(dealMapper.toDto(testDeal)).thenReturn(testDealDto);

        List<DealDto> result = dealService.getActiveDealsForProduct(productId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testDealDto, result.get(0));
        verify(dealRepository).findActiveDealsForProduct(eq(productId), any(LocalDateTime.class));
    }

    @Test
    void getActiveDealsForProduct_EmptyList() {
        Long productId = 1L;
        when(dealRepository.findActiveDealsForProduct(eq(productId), any(LocalDateTime.class))).thenReturn(Arrays.asList());

        List<DealDto> result = dealService.getActiveDealsForProduct(productId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(dealRepository).findActiveDealsForProduct(eq(productId), any(LocalDateTime.class));
    }

    @Test
    void deleteDeal_Success() {
        Long dealId = 1L;
        when(dealRepository.findById(dealId)).thenReturn(Optional.of(testDeal));
        when(dealRepository.save(any(Deal.class))).thenReturn(testDeal);

        dealService.deleteDeal(dealId);

        verify(dealRepository).findById(dealId);
        verify(dealRepository).save(any(Deal.class));
        assertTrue(testDeal.getIsDelete());
    }

    @Test
    void deleteDeal_NotFound() {
        Long dealId = 999L;
        when(dealRepository.findById(dealId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> dealService.deleteDeal(dealId));
        verify(dealRepository).findById(dealId);
        verify(dealRepository, never()).save(any());
    }

    @Test
    void expireDeals_Success() {
        List<Deal> expiredDeals = Arrays.asList(testDeal);
        when(dealRepository.findByExpiresAtBeforeAndIsDeleteFalse(any(LocalDateTime.class))).thenReturn(expiredDeals);
        when(dealRepository.saveAll(expiredDeals)).thenReturn(expiredDeals);

        dealService.expireDeals();

        verify(dealRepository).findByExpiresAtBeforeAndIsDeleteFalse(any(LocalDateTime.class));
        verify(dealRepository).saveAll(expiredDeals);
        assertTrue(testDeal.getIsDelete());
    }

    @Test
    void expireDeals_NoExpiredDeals() {
        when(dealRepository.findByExpiresAtBeforeAndIsDeleteFalse(any(LocalDateTime.class))).thenReturn(Arrays.asList());

        dealService.expireDeals();

        verify(dealRepository).findByExpiresAtBeforeAndIsDeleteFalse(any(LocalDateTime.class));
        verify(dealRepository, never()).saveAll(any());
    }
} 