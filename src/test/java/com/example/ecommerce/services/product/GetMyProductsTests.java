package com.example.ecommerce.services.product;

import com.example.ecommerce.BaseTest.product.BaseTestProduct;
import com.example.ecommerce.dtos.product.MyProductSummaryProjection;
import com.example.ecommerce.dtos.product.MyProductSummaryResponse;
import com.example.ecommerce.entitites.product.enums.Status;
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
public class GetMyProductsTests extends BaseTestProduct {
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
                () -> productService.getMyProducts(authentication, Status.ACTIVE, Pageable.unpaged())
        );

        verifyNoInteractions(productRepository);
    }

    @Test
    void shouldReturnMyProducts() {
        when(currentUserProvider.get(authentication))
                .thenReturn(user);

        MyProductSummaryProjection projection = mock(MyProductSummaryProjection.class);

        when(projection.getId()).thenReturn(product.getId());
        when(projection.getImageUrl()).thenReturn(image.getUrl());
        when(projection.getLikeCount()).thenReturn(5L);
        when(projection.getViewCount()).thenReturn(10L);

        Page<MyProductSummaryProjection> page = new PageImpl<>(List.of(projection));

        when(productRepository.findBySellerAndStatus(user, Status.ACTIVE, Pageable.unpaged()))
                .thenReturn(page);

        Page<MyProductSummaryResponse> result =
                productService.getMyProducts(authentication, Status.ACTIVE, Pageable.unpaged());

        MyProductSummaryResponse first = result.getContent().getFirst();

        assertEquals(1, result.getTotalElements());
        assertEquals(projection.getId(), first.getId());
        assertEquals(projection.getImageUrl(), first.getImageUrl());
        assertEquals(projection.getLikeCount(), first.getLikeCount());
        assertEquals(projection.getViewCount(), first.getViewCount());

        verify(productRepository).findBySellerAndStatus(user, Status.ACTIVE, Pageable.unpaged());
    }
}
