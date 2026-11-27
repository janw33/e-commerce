package com.example.ecommerce.services.product;

import com.example.ecommerce.BaseTest.product.BaseTestProduct;
import com.example.ecommerce.dtos.product.ProductResponse;
import com.example.ecommerce.entitites.product.enums.*;
import com.example.ecommerce.entitites.product.productEntities.Image;
import com.example.ecommerce.exceptions.brand.BrandNotFoundException;
import com.example.ecommerce.exceptions.category.CategoryNotFoundException;
import com.example.ecommerce.exceptions.image.InvalidImageCountException;
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
import com.example.ecommerce.services.image.ImageUploadValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
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

    @Mock
    private ImageUploadValidator imageUploadValidator;

    @Captor
    private ArgumentCaptor<List<Image>> imagesCaptor;

    @Captor
    private ArgumentCaptor<List<String>> fileNamesCaptor;

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
                () -> productService.updateProduct(updateProductRequest, List.of(imageFile), authentication, product.getId())
        );

        verifyNoInteractions(productRepository, categoryRepository, brandRepository,
                likeRepository, imageRepository, imageUploadValidator);
    }

    @Test
    void shouldThrowProductNotFoundExceptionWhenProductDoesntExists() {
        when(currentUserProvider.get(authentication))
                .thenReturn(user);

        when(productRepository.findBySellerAndIdAndStatus(user, product.getId(), Status.ACTIVE))
                .thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> productService.updateProduct(updateProductRequest, List.of(imageFile), authentication, product.getId())
        );

        verifyNoInteractions(categoryRepository, brandRepository, likeRepository,
                imageRepository, imageUploadValidator);
    }

    @Test
    void shouldThrowInvalidImageCountExceptionWhenMoreThanTwentyImagesAreUploaded() {
        List<MultipartFile> uploads = Collections.nCopies(21, imageFile);

        when(currentUserProvider.get(authentication))
                .thenReturn(user);

        when(productRepository.findBySellerAndIdAndStatus(user, product.getId(), Status.ACTIVE))
                .thenReturn(Optional.of(product));

        assertThrows(
                InvalidImageCountException.class,
                () -> productService.updateProduct(updateProductRequest, uploads, authentication, product.getId())
        );

        verifyNoInteractions(categoryRepository, brandRepository, likeRepository,
                imageRepository, imageUploadValidator);
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
                () -> productService.updateProduct(updateProductRequest, List.of(imageFile), authentication, product.getId())
        );

        verifyNoInteractions(brandRepository, likeRepository, imageRepository, imageUploadValidator);
    }

    @Test
    void shouldThrowInvalidSizeTypeExceptionWhenCategoryAndProductSizeTypesDontMatch() {
        category.setSizeType(SizeType.SHOES);

        when(currentUserProvider.get(authentication))
                .thenReturn(user);

        when(productRepository.findBySellerAndIdAndStatus(user, product.getId(), Status.ACTIVE))
                .thenReturn(Optional.of(product));

        when(categoryRepository.findById(updateProductRequest.getCategoryId()))
                .thenReturn(Optional.of(category));

        assertThrows(
                InvalidSizeTypeException.class,
                () -> productService.updateProduct(updateProductRequest, List.of(imageFile), authentication, product.getId())
        );

        verifyNoInteractions(brandRepository, likeRepository, imageRepository, imageUploadValidator);
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
                () -> productService.updateProduct(updateProductRequest, List.of(imageFile), authentication, product.getId())
        );

        verifyNoInteractions(likeRepository, imageRepository, imageUploadValidator);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldUpdateProductAndKeepExistingImagesWhenNoImagesAreUploaded(List<MultipartFile> uploads) throws IOException {
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

        when(imageRepository.findAllByProductIdOrderByPositionAsc(product.getId()))
                .thenReturn(List.of(image));

        ProductResponse result = productService.updateProduct(updateProductRequest, uploads, authentication, product.getId());

        assertEquals(updateProductRequest.getTitle(), product.getTitle());
        assertEquals(updateProductRequest.getDescription(), product.getDescription());
        assertEquals(updateProductRequest.getPrice(), product.getPrice());
        assertEquals(updateProductRequest.getColors(), product.getColors());
        assertEquals(updateProductRequest.getMaterials(), product.getMaterials());
        assertEquals(updateProductRequest.getCondition(), product.getCondition());
        assertEquals(updateProductRequest.getShippingSize(), product.getShippingSize());
        assertEquals(updateProductRequest.getSize(), product.getSize());
        assertSame(category, product.getCategory());
        assertSame(brand, product.getBrand());

        assertEquals(product.getTitle(), result.getTitle());
        assertEquals(product.getDescription(), result.getDescription());
        assertEquals(product.getPrice(), result.getPrice());
        assertEquals(product.getColors(), result.getColors());
        assertEquals(product.getMaterials(), result.getMaterials());
        assertEquals(product.getCondition(), result.getCondition());
        assertEquals(product.getShippingSize(), result.getShippingSize());
        assertEquals(product.getSize(), result.getSize());
        assertEquals(category.getId(), result.getCategoryId());
        assertEquals(brand.getId(), result.getBrandId());
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
        assertEquals(1, result.getImages().size());
        assertEquals(image.getId(), result.getImages().getFirst().getId());
        assertEquals(image.getUrl(), result.getImages().getFirst().getUrl());

        verify(productRepository).flush();
        verify(imageRepository).findAllByProductIdOrderByPositionAsc(product.getId());
        verifyNoMoreInteractions(imageRepository);
        verifyNoInteractions(imageUploadValidator);
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 20})
    void shouldReplaceImagesInUploadOrder(int imageCount) throws IOException {
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

        when(imageRepository.findAllByProductIdOrderByPositionAsc(product.getId()))
                .thenReturn(List.of(image));

        List<MultipartFile> uploads = Collections.nCopies(imageCount, imageFile);

        List<String> fileNames = IntStream.range(0, imageCount)
                .mapToObj(position -> "image-" + position + ".png")
                .toList();

        when(imageUploadValidator.store(imageFile))
                .thenReturn(fileNames.getFirst(), fileNames.subList(1, imageCount).toArray(String[]::new));

        when(imageRepository.saveAll(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponse result = productService.updateProduct(updateProductRequest, uploads, authentication, product.getId());

        InOrder inOrder = inOrder(imageUploadValidator, imageRepository);
        inOrder.verify(imageUploadValidator).deleteFilesOnRollback(fileNamesCaptor.capture());
        inOrder.verify(imageUploadValidator, times(imageCount)).store(imageFile);
        inOrder.verify(imageRepository).deleteAll(List.of(image));
        inOrder.verify(imageRepository).flush();
        inOrder.verify(imageRepository).saveAll(imagesCaptor.capture());

        List<Image> savedImages = imagesCaptor.getValue();
        assertEquals(imageCount, savedImages.size());
        assertEquals(imageCount, result.getImages().size());
        assertEquals(fileNames, fileNamesCaptor.getValue());

        for (int position = 0; position < imageCount; position++) {
            Image savedImage = savedImages.get(position);
            String expectedUrl = "/api/v1/image/files/" + fileNames.get(position);
            assertSame(product, savedImage.getProduct());
            assertEquals(position, savedImage.getPosition());
            assertEquals(expectedUrl, savedImage.getUrl());
            assertEquals(expectedUrl, result.getImages().get(position).getUrl());
        }
    }

    @Test
    void shouldKeepExistingImagesAndPropagateIOExceptionWhenUploadFails() throws IOException {
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

        when(imageRepository.findAllByProductIdOrderByPositionAsc(product.getId()))
                .thenReturn(List.of(image));

        IOException failure = new IOException("Could not store image");

        when(imageUploadValidator.store(imageFile))
                .thenReturn("first.png")
                .thenThrow(failure);

        IOException exception = assertThrows(
                IOException.class,
                () -> productService.updateProduct(updateProductRequest, List.of(imageFile, imageFile), authentication, product.getId())
        );

        assertSame(failure, exception);
        verify(imageUploadValidator).deleteFilesOnRollback(fileNamesCaptor.capture());
        assertEquals(List.of("first.png"), fileNamesCaptor.getValue());
        verify(imageRepository).findAllByProductIdOrderByPositionAsc(product.getId());
        verifyNoMoreInteractions(imageRepository);
    }
}
