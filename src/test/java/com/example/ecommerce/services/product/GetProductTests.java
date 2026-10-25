package com.example.ecommerce.services.product;

import com.example.ecommerce.BaseTest.product.BaseTestProduct;
import com.example.ecommerce.dtos.image.ImageResponse;
import com.example.ecommerce.dtos.product.ProductResponse;
import com.example.ecommerce.entitites.product.enums.Status;
import com.example.ecommerce.entitites.product.productEntities.Image;
import com.example.ecommerce.exceptions.product.ProductNotFoundException;
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
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GetProductTests extends BaseTestProduct {
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
                () -> productService.getProduct(product.getId(), authentication)
        );

        verifyNoInteractions(productRepository, likeRepository, imageRepository);
    }

    @Test
    void shouldThrowProductNotFoundExceptionWhenProductDoesntExist() {
        when(currentUserProvider.get(authentication))
                .thenReturn(user);

        when(productRepository.findByIdAndStatusIn(product.getId(), List.of(Status.ACTIVE, Status.SOLD)))
                .thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> productService.getProduct(product.getId(), authentication)
        );

        verify(likeRepository, never()).countByProductId(any());
        verify(imageRepository, never()).findAllByProductIdOrderByPositionAsc(any());
    }

    @Test
    void shouldReturnProduct() {
        when(currentUserProvider.get(authentication))
                .thenReturn(user);

        when(productRepository.findByIdAndStatusIn(product.getId(), List.of(Status.ACTIVE, Status.SOLD)))
                .thenReturn(Optional.of(product));

        when(likeRepository.countByProductId(product.getId()))
                .thenReturn(2L);

        when(imageRepository.findAllByProductIdOrderByPositionAsc(product.getId()))
                .thenReturn(List.of(image));

        ProductResponse result = productService.getProduct(product.getId(), authentication);

        assertEquals(product.getId(), result.getId());
        assertEquals(product.getSeller().getId(), result.getSellerId());
        assertEquals(product.getSeller().getFirstName(), result.getSellerUsername());
        assertEquals(product.getCategory().getId(), result.getCategoryId());
        assertEquals(product.getBrand().getId(), result.getBrandId());
        assertEquals(product.getBrand().getName(), result.getBrandName());
        assertEquals(product.getTitle(), result.getTitle());
        assertEquals(product.getDescription(), result.getDescription());
        assertEquals(product.getPrice(), result.getPrice());
        assertEquals(product.getColors(), result.getColors());
        assertEquals(product.getCondition(), result.getCondition());
        assertEquals(product.getMaterials(), result.getMaterials());
        assertEquals(product.getSize(), result.getSize());
        assertEquals(product.getShippingSize(), result.getShippingSize());
        assertEquals(product.getViewCount(), result.getViewCount());
        assertEquals(2L, result.getLikeCount());
        assertEquals(product.getStatus(), result.getStatus());
        assertEquals(product.getCreatedAt(), result.getCreatedAt());
        assertEquals(product.getUpdatedAt(), result.getUpdatedAt());
        assertEquals(product.getDeletedAt(), result.getDeletedAt());
        assertEquals(product.getSoldAt(), result.getSoldAt());
        assertEquals(
                Stream.of(image).map(Image::getUrl).toList(),
                result.getImages().stream().map(ImageResponse::getUrl).toList()
        );

        verify(likeRepository).countByProductId(product.getId());
        verify(imageRepository).findAllByProductIdOrderByPositionAsc(product.getId());
    }
}
