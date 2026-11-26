package com.example.ecommerce.services.product;

import com.example.ecommerce.BaseTest.product.BaseTestProduct;
import com.example.ecommerce.dtos.product.ProductResponse;
import com.example.ecommerce.entitites.product.Product;
import com.example.ecommerce.entitites.product.enums.SizeType;
import com.example.ecommerce.entitites.product.enums.Status;
import com.example.ecommerce.entitites.product.productEntities.Image;
import com.example.ecommerce.exceptions.brand.BrandNotFoundException;
import com.example.ecommerce.exceptions.category.CategoryNotFoundException;
import com.example.ecommerce.exceptions.image.InvalidImageCountException;
import com.example.ecommerce.exceptions.product.InvalidSizeTypeException;
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
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateProductTests extends BaseTestProduct {
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
                () -> productService.createProduct(createProductRequest, List.of(imageFile), authentication)
        );

        verify(productRepository, never()).save(any());
        verify(imageRepository, never()).saveAll(any());
    }

    @Test
    void shouldThrowInvalidImageCountExceptionWhenImagesAreEmpty() {
        when(currentUserProvider.get(authentication))
                .thenReturn(user);

        assertThrows(
                InvalidImageCountException.class,
                () -> productService.createProduct(createProductRequest, List.of(), authentication)
        );

        verify(productRepository, never()).save(any());
        verify(imageRepository, never()).saveAll(any());
    }

    @Test
    void shouldThrowInvalidImageCountExceptionWhenImagesSizeIsAbove20() {
        List<MultipartFile> list = Collections.nCopies(21, imageFile);

        when(currentUserProvider.get(authentication))
                .thenReturn(user);

        assertThrows(
                InvalidImageCountException.class,
                () -> productService.createProduct(createProductRequest, list, authentication)
        );

        verify(productRepository, never()).save(any());
        verify(imageRepository, never()).saveAll(any());
    }

    @Test
    void shouldThrowCategoryNotFoundExceptionWhenCategoryDoesntExists() {
        when(currentUserProvider.get(authentication))
                .thenReturn(user);

        when(categoryRepository.findById(category.getId()))
                .thenReturn(Optional.empty());

        assertThrows(
                CategoryNotFoundException.class,
                () -> productService.createProduct(createProductRequest, List.of(imageFile), authentication)
        );

        verify(productRepository, never()).save(any());
        verify(imageRepository, never()).saveAll(any());
    }

    @Test
    void shouldThrowInvalidSizeTypeExceptionWhenCategorySizeTypeAndProductSizeTypeDoesntMatch() {
        category.setSizeType(SizeType.SHOES);

        when(currentUserProvider.get(authentication))
                .thenReturn(user);

        when(categoryRepository.findById(category.getId()))
                .thenReturn(Optional.of(category));

        assertThrows(
                InvalidSizeTypeException.class,
                () -> productService.createProduct(createProductRequest, List.of(imageFile), authentication)
        );

        verify(productRepository, never()).save(any());
        verify(imageRepository, never()).saveAll(any());
    }

    @Test
    void shouldThrowBrandNotFoundExceptionWhenBrandDoesntExists() {
        when(currentUserProvider.get(authentication))
                .thenReturn(user);

        when(categoryRepository.findById(category.getId()))
                .thenReturn(Optional.of(category));

        when(brandRepository.findById(brand.getId()))
                .thenReturn(Optional.empty());

        assertThrows(
                BrandNotFoundException.class,
                () -> productService.createProduct(createProductRequest,List.of(imageFile), authentication)
        );

        verify(productRepository, never()).save(any());
        verify(imageRepository, never()).saveAll(any());
    }

    @Test
    void shouldCreateProduct() throws IOException {
        when(currentUserProvider.get(authentication))
                .thenReturn(user);

        when(categoryRepository.findById(category.getId()))
                .thenReturn(Optional.of(category));

        when(brandRepository.findById(brand.getId()))
                .thenReturn(Optional.of(brand));

        when(productRepository.save(any()))
                .thenReturn(product);

        when(imageRepository.saveAll(any()))
                .thenReturn(List.of(image));

        when(imageUploadValidator.store(imageFile)).thenReturn("stored-image.png");

        ProductResponse result = productService.createProduct(createProductRequest, List.of(imageFile), authentication);

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
        assertEquals(0, result.getLikeCount());
        assertEquals(product.getStatus(), result.getStatus());
        assertEquals(product.getCreatedAt(), result.getCreatedAt());
        assertEquals(product.getUpdatedAt(), result.getUpdatedAt());
        assertEquals(product.getDeletedAt(), result.getDeletedAt());
        assertEquals(product.getSoldAt(), result.getSoldAt());
        assertNotNull(result.getImages());
        assertEquals(1, result.getImages().size());

        assertEquals(image.getUrl(), result.getImages().getFirst().getUrl());

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);

        verify(productRepository).save(productCaptor.capture());

        Product savedProduct = productCaptor.getValue();

        assertSame(user, savedProduct.getSeller());
        assertSame(category, savedProduct.getCategory());
        assertSame(brand, savedProduct.getBrand());

        assertEquals(createProductRequest.getTitle(), savedProduct.getTitle());
        assertEquals(createProductRequest.getDescription(), savedProduct.getDescription());
        assertEquals(createProductRequest.getPrice(), savedProduct.getPrice());
        assertEquals(createProductRequest.getColors(), savedProduct.getColors());
        assertEquals(createProductRequest.getCondition(), savedProduct.getCondition());
        assertEquals(createProductRequest.getMaterials(), savedProduct.getMaterials());
        assertEquals(createProductRequest.getSize(), savedProduct.getSize());
        assertEquals(createProductRequest.getShippingSize(), savedProduct.getShippingSize());

        assertEquals(Status.ACTIVE, savedProduct.getStatus());
        assertEquals(0L, savedProduct.getViewCount());
        assertNull(savedProduct.getDeletedAt());
        assertNull(savedProduct.getSoldAt());

        ArgumentCaptor<List<Image>> captor = ArgumentCaptor.forClass(List.class);

        verify(imageRepository).saveAll(captor.capture());

        List<Image> savedImages = captor.getValue();

        assertEquals(1, savedImages.size());
        assertEquals(0, savedImages.getFirst().getPosition());
        assertSame(product, savedImages.getFirst().getProduct());
        assertEquals("/api/v1/image/files/stored-image.png",
                savedImages.getFirst().getUrl());}
}
