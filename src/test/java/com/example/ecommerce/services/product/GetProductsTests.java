package com.example.ecommerce.services.product;

import com.example.ecommerce.BaseTest.product.BaseTestProduct;
import com.example.ecommerce.dtos.product.ProductSummaryProjection;
import com.example.ecommerce.dtos.product.ProductSummaryResponse;
import com.example.ecommerce.entitites.product.enums.ProductSortBy;
import com.example.ecommerce.exceptions.user.UserNotFoundException;
import com.example.ecommerce.mappers.image.ImageMapper;
import com.example.ecommerce.mappers.product.ProductMapper;
import com.example.ecommerce.providers.CurrentUserProvider;
import com.example.ecommerce.repository.brand.BrandRepository;
import com.example.ecommerce.repository.category.CategoryRepository;
import com.example.ecommerce.repository.image.ImageRepository;
import com.example.ecommerce.repository.like.LikeRepository;
import com.example.ecommerce.repository.product.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GetProductsTests extends BaseTestProduct {
    @Mock
    private CurrentUserProvider currentUserProvider;

    @Mock
    private Authentication authentication;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private LikeRepository likeRepository;

    private final ProductMapper productMapper = new ProductMapper();

    private final ImageMapper imageMapper = new ImageMapper();

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(currentUserProvider,
                productRepository,
                imageRepository,
                categoryRepository,
                brandRepository,
                likeRepository,
                productMapper,
                imageMapper
        );
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenUserDoesntExists() {
        when(currentUserProvider.get(authentication))
                .thenThrow(new UserNotFoundException());

        assertThrows(
                UserNotFoundException.class,
                () -> productService.getProducts(null, null, null, null,
                        null, null, null, null, null, null, authentication, Pageable.unpaged())
        );

        verify(productRepository, never()).getProducts(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void shouldReturnProducts() {
        when(currentUserProvider.get(authentication))
                .thenReturn(user);

        ProductSummaryProjection projection = mock(ProductSummaryProjection.class);

        when(projection.getId()).thenReturn(product.getId());
        when(projection.getTitle()).thenReturn(product.getTitle());
        when(projection.getImageUrl()).thenReturn(image.getUrl());
        when(projection.getPrice()).thenReturn(product.getPrice());
        when(projection.getCondition()).thenReturn(product.getCondition());
        when(projection.getSize()).thenReturn(product.getSize());
        when(projection.getBrandName()).thenReturn(product.getBrand().getName());
        when(projection.getLikeCount()).thenReturn(0L);

        Page<ProductSummaryProjection> page = new PageImpl<>(List.of(projection));

        when(productRepository.getProducts(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(page);

        Page<ProductSummaryResponse> result = productService.getProducts(null, null, null, null,
                null, null, null, null, ProductSortBy.RELEVANCE, null, authentication, Pageable.unpaged());

        ProductSummaryResponse first = result.getContent().getFirst();

        assertEquals(projection.getId(), first.getId());
        assertEquals(projection.getTitle(), first.getTitle());
        assertEquals(projection.getImageUrl(), first.getImageUrl());
        assertEquals(projection.getPrice(), first.getPrice());
        assertEquals(projection.getCondition(), first.getCondition());
        assertEquals(projection.getSize(), first.getSize());
        assertEquals(projection.getBrandName(), first.getBrandName());
        assertEquals(projection.getLikeCount(), first.getLikeCount());

        verify(productRepository).getProducts(any(), any(), any(), any(), any(), any(), any(), any(), eq(ProductSortBy.RELEVANCE.name()), any(), any());
    }
}
