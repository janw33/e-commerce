package com.example.ecommerce.services.image;

import com.example.ecommerce.BaseTest.product.BaseTestProduct;
import com.example.ecommerce.dtos.image.ImageResponse;
import com.example.ecommerce.entitites.product.enums.Status;
import com.example.ecommerce.exceptions.product.ProductNotFoundException;
import com.example.ecommerce.exceptions.user.UserNotFoundException;
import com.example.ecommerce.mappers.image.ImageMapper;
import com.example.ecommerce.providers.CurrentUserProvider;
import com.example.ecommerce.repository.image.ImageRepository;
import com.example.ecommerce.repository.product.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GetImagesTests extends BaseTestProduct {
    @Mock
    private CurrentUserProvider currentUserProvider;

    @Mock
    private Authentication authentication;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ImageRepository imageRepository;

    private final ImageMapper imageMapper = new ImageMapper();

    private ImageService imageService;

    @BeforeEach
    void setUp() {
        imageService = new ImageService(
                currentUserProvider,
                productRepository,
                imageRepository,
                imageMapper
        );
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenUserDoesntExists() {
        when(currentUserProvider.get(authentication))
                .thenThrow(new UserNotFoundException());

        assertThrows(
                UserNotFoundException.class,
                () -> imageService.getImages(authentication, product.getId())
        );

        verifyNoInteractions(productRepository, imageRepository);
    }

    @Test
    void shouldThrowProductNotFoundExceptionWhenProductDoesntExist() {
        when(currentUserProvider.get(authentication))
                .thenReturn(user);

        when(productRepository.findByIdAndStatusIn(product.getId(), List.of(Status.ACTIVE, Status.SOLD)))
                .thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> imageService.getImages(authentication, product.getId())
        );

        verifyNoInteractions(imageRepository);
    }

    @Test
    void shouldReturnImages() {
        when(currentUserProvider.get(authentication))
                .thenReturn(user);

        when(productRepository.findByIdAndStatusIn(product.getId(), List.of(Status.ACTIVE, Status.SOLD)))
                .thenReturn(Optional.of(product));

        when(imageRepository.findAllByProductIdOrderByPositionAsc(product.getId()))
                .thenReturn(List.of(image));

        List<ImageResponse> result = imageService.getImages(authentication, product.getId());

        assertEquals(1, result.size());
        assertEquals(image.getId(), result.getFirst().getId());
        assertEquals(image.getUrl(), result.getFirst().getUrl());

        verify(imageRepository).findAllByProductIdOrderByPositionAsc(product.getId());
    }
}
