package com.example.ecommerce.services.product;

import com.example.ecommerce.BaseTest.product.BaseTestProduct;
import com.example.ecommerce.entitites.product.enums.Status;
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
import com.example.ecommerce.services.image.ImageUploadValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeleteProductTests extends BaseTestProduct {
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

    @Mock
    private ImageUploadValidator imageUploadValidator;

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
                imageMapper,
                imageUploadValidator
        );
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenUserDoesntExists() {
        when(currentUserProvider.get(authentication))
                .thenThrow(new UserNotFoundException());

        assertThrows(
                UserNotFoundException.class,
                () -> productService.deleteProduct(product.getId(), authentication)
        );

        verifyNoInteractions(productRepository, imageRepository);
    }

    @Test
    void shouldThrowProductNotFoundExceptionWhenProductDoesntExists() {
        when(currentUserProvider.get(authentication))
                .thenReturn(user);

        when(productRepository.findBySellerAndIdAndStatusIn(user, product.getId(), List.of(Status.ACTIVE, Status.SOLD)))
                .thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> productService.deleteProduct(product.getId(), authentication)
        );

        verifyNoInteractions(imageRepository);
    }

    @Test
    void shouldDeleteProductAndItsImages() {
        when(currentUserProvider.get(authentication))
                .thenReturn(user);

        when(productRepository.findBySellerAndIdAndStatusIn(user, product.getId(), List.of(Status.ACTIVE, Status.SOLD)))
                .thenReturn(Optional.of(product));

        productService.deleteProduct(product.getId(), authentication);

        assertEquals(Status.DELETED, product.getStatus());
        assertNotNull(product.getDeletedAt());

        verify(imageRepository).deleteAllByProductId(product.getId());
        verify(productRepository).findBySellerAndIdAndStatusIn(user, product.getId(), List.of(Status.ACTIVE, Status.SOLD));
    }
}
