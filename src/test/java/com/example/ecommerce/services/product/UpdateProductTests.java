package com.example.ecommerce.services.product;

import com.example.ecommerce.BaseTest.product.BaseTestProduct;
import com.example.ecommerce.dtos.product.ProductResponse;
import com.example.ecommerce.dtos.product.UpdateProductRequest;
import com.example.ecommerce.entitites.product.enums.*;
import com.example.ecommerce.entitites.product.productEntities.Image;
import com.example.ecommerce.exceptions.brand.BrandNotFoundException;
import com.example.ecommerce.exceptions.category.CategoryNotFoundException;
import com.example.ecommerce.exceptions.product.InvalidSizeTypeException;
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
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UpdateProductTests extends BaseTestProduct {
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
                () -> productService.updateProduct(updateProductRequest, authentication, product.getId())
        );

        verifyNoInteractions(productRepository, categoryRepository, brandRepository, likeRepository, imageRepository);
    }

    @Test
    void shouldThrowProductNotFoundExceptionWhenProductDoesntExists() {
        when(currentUserProvider.get(authentication))
                .thenReturn(user);

        when(productRepository.findBySellerAndIdAndStatus(user, product.getId(), Status.ACTIVE))
                .thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> productService.updateProduct(updateProductRequest, authentication, product.getId())
        );

        verifyNoInteractions(categoryRepository, brandRepository, likeRepository, imageRepository);
    }


    @Test
    void shouldThrowCategoryNotFoundExceptionWhenCategoryDoesntExists() {
        when(currentUserProvider.get(authentication))
                .thenReturn(user);

        when(productRepository.findBySellerAndIdAndStatus(user, product.getId(), Status.ACTIVE))
                .thenReturn(Optional.of(product));

        when(categoryRepository.findById(updateProductRequest.getCategoryId()))
                .thenReturn(Optional.empty());

        assertThrows(
                CategoryNotFoundException.class,
                () -> productService.updateProduct(updateProductRequest, authentication, product.getId())
        );

        verifyNoInteractions(brandRepository, likeRepository, imageRepository);
    }

    @Test
    void shouldThrowInvalidSizeTypeExceptionWhenCategorySizeTypeAndProductSizeTypeDoesntMatch() {
        category.setSizeType(SizeType.SHOES);

        when(currentUserProvider.get(authentication))
                .thenReturn(user);

        when(productRepository.findBySellerAndIdAndStatus(user, product.getId(), Status.ACTIVE))
                .thenReturn(Optional.of(product));

        when(categoryRepository.findById(category.getId()))
                .thenReturn(Optional.of(category));

        assertThrows(
                InvalidSizeTypeException.class,
                () -> productService.updateProduct(updateProductRequest, authentication, product.getId())
        );

        verifyNoInteractions(brandRepository, likeRepository, imageRepository);
    }

    @Test
    void shouldThrowBrandNotFoundExceptionWhenBrandDoesntExists() {
        when(currentUserProvider.get(authentication))
                .thenReturn(user);

        when(productRepository.findBySellerAndIdAndStatus(user, product.getId(), Status.ACTIVE))
                .thenReturn(Optional.of(product));

        when(categoryRepository.findById(updateProductRequest.getCategoryId()))
                .thenReturn(Optional.of(category));

        when(brandRepository.findById(updateProductRequest.getBrandId()))
                .thenReturn(Optional.empty());

        assertThrows(
                BrandNotFoundException.class,
                () -> productService.updateProduct(updateProductRequest, authentication, product.getId())
        );

        verifyNoInteractions(likeRepository, imageRepository);
    }

    @Test
    void shouldUpdateProduct() {
        updateProductRequest.setTitle("New Title");
        updateProductRequest.setDescription("new description");
        updateProductRequest.setPrice(new BigDecimal("42.50"));
        updateProductRequest.setColors(Set.of(Color.RED));
        updateProductRequest.setMaterials(Set.of(Material.WOOL));
        updateProductRequest.setCondition(Condition.GOOD);
        updateProductRequest.setShippingSize(ShippingSize.LARGE);

        when(currentUserProvider.get(authentication))
                .thenReturn(user);

        when(productRepository.findBySellerAndIdAndStatus(user, product.getId(), Status.ACTIVE))
                .thenReturn(Optional.of(product));

        when(categoryRepository.findById(updateProductRequest.getCategoryId()))
                .thenReturn(Optional.of(category));

        when(brandRepository.findById(updateProductRequest.getBrandId()))
                .thenReturn(Optional.of(brand));

        when(likeRepository.countByProductId(product.getId()))
                .thenReturn(3L);

        when(imageRepository.saveAll(any()))
                .thenReturn(List.of(image));

        ProductResponse result = productService.updateProduct(updateProductRequest, authentication, product.getId());

        assertEquals(updateProductRequest.getTitle(), product.getTitle());
        assertEquals(updateProductRequest.getDescription(), product.getDescription());
        assertEquals(updateProductRequest.getPrice(), product.getPrice());
        assertEquals(updateProductRequest.getColors(), product.getColors());
        assertEquals(updateProductRequest.getMaterials(), product.getMaterials());
        assertEquals(updateProductRequest.getCondition(), product.getCondition());
        assertEquals(updateProductRequest.getShippingSize(), product.getShippingSize());
        assertEquals(updateProductRequest.getSize(), product.getSize());
        assertEquals(updateProductRequest.getCategoryId(), product.getCategory().getId());
        assertEquals(updateProductRequest.getBrandId(), product.getBrand().getId());

        assertEquals(updateProductRequest.getTitle(), result.getTitle());
        assertEquals(updateProductRequest.getDescription(), result.getDescription());
        assertEquals(updateProductRequest.getPrice(), result.getPrice());
        assertEquals(updateProductRequest.getColors(), result.getColors());
        assertEquals(updateProductRequest.getMaterials(), result.getMaterials());
        assertEquals(updateProductRequest.getCondition(), result.getCondition());
        assertEquals(updateProductRequest.getShippingSize(), result.getShippingSize());
        assertEquals(updateProductRequest.getSize(), result.getSize());
        assertEquals(updateProductRequest.getCategoryId(), result.getCategoryId());
        assertEquals(updateProductRequest.getBrandId(), result.getBrandId());
        assertEquals(brand.getName(), result.getBrandName());

        assertEquals(product.getId(), result.getId());
        assertEquals(user.getId(), result.getSellerId());
        assertEquals(user.getFirstName(), result.getSellerUsername());
        assertEquals(product.getViewCount(), result.getViewCount());
        assertEquals(3L, result.getLikeCount());
        assertEquals(Status.ACTIVE, result.getStatus());
        assertEquals(product.getCreatedAt(), result.getCreatedAt());
        assertEquals(product.getUpdatedAt(), result.getUpdatedAt());
        assertEquals(product.getDeletedAt(), result.getDeletedAt());
        assertEquals(product.getSoldAt(), result.getSoldAt());

        assertNotNull(result.getImages());
        assertEquals(1, result.getImages().size());
        assertEquals(image.getId(), result.getImages().getFirst().getId());
        assertEquals(image.getUrl(), result.getImages().getFirst().getUrl());

        InOrder inOrder = inOrder(imageRepository);

        inOrder.verify(imageRepository).deleteAllByProductId(product.getId());
        inOrder.verify(imageRepository).flush();
        inOrder.verify(imageRepository).saveAll(any());

        ArgumentCaptor<List<Image>> captor = ArgumentCaptor.forClass(List.class);

        verify(imageRepository).saveAll(captor.capture());

        List<Image> savedImages = captor.getValue();

        assertEquals(1, savedImages.size());
        assertEquals(product, savedImages.getFirst().getProduct());
        assertEquals(updateProductRequest.getImages().getFirst().getUrl(), savedImages.getFirst().getUrl());
        assertEquals(0, savedImages.getFirst().getPosition());

        verify(productRepository).findBySellerAndIdAndStatus(user, product.getId(), Status.ACTIVE);
        verify(categoryRepository).findById(updateProductRequest.getCategoryId());
        verify(brandRepository).findById(updateProductRequest.getBrandId());
        verify(likeRepository).countByProductId(product.getId());
    }
}