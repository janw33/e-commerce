package com.example.ecommerce.services.like;

import com.example.ecommerce.BaseTest.product.BaseTestProduct;
import com.example.ecommerce.entitites.product.enums.Status;
import com.example.ecommerce.exceptions.like.LikeNotFoundException;
import com.example.ecommerce.exceptions.product.ProductNotFoundException;
import com.example.ecommerce.exceptions.user.UserNotFoundException;
import com.example.ecommerce.providers.CurrentUserProvider;
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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UnlikeProductTests extends BaseTestProduct {
    @Mock
    private CurrentUserProvider currentUserProvider;

    @Mock
    private Authentication authentication;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private LikeRepository likeRepository;

    private LikeService likeService;

    @BeforeEach
    void setUp() {
        likeService = new LikeService(
                likeRepository,
                productRepository,
                currentUserProvider);
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenUserDoesntExists() {
        when(currentUserProvider.get(authentication))
                .thenThrow(new UserNotFoundException());

        assertThrows(
                UserNotFoundException.class,
                () -> likeService.unlikeProduct(product.getId(), authentication)
        );

        verifyNoInteractions(productRepository, likeRepository);
    }

    @Test
    void shouldThrowProductNotFoundExceptionWhenActiveOrSoldProductDoesntExists() {
        when(currentUserProvider.get(authentication))
                .thenReturn(user);

        when(productRepository.findByIdAndStatusIn(product.getId(), List.of(Status.ACTIVE, Status.SOLD)))
                .thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> likeService.unlikeProduct(product.getId(), authentication)
        );

        verifyNoInteractions(likeRepository);
    }

    @Test
    void shouldThrowLikeNotFoundExceptionWhenProductIsNotLiked() {
        when(currentUserProvider.get(authentication))
                .thenReturn(user);

        when(productRepository.findByIdAndStatusIn(product.getId(), List.of(Status.ACTIVE, Status.SOLD)))
                .thenReturn(Optional.of(product));

        when(likeRepository.deleteLike(user.getId(), product.getId()))
                .thenReturn(0);

        assertThrows(
                LikeNotFoundException.class,
                () -> likeService.unlikeProduct(product.getId(), authentication)
        );

        verify(likeRepository).deleteLike(user.getId(), product.getId());
    }

    @Test
    void shouldUnlikeActiveProduct() {
        when(currentUserProvider.get(authentication))
                .thenReturn(user);

        when(productRepository.findByIdAndStatusIn(product.getId(), List.of(Status.ACTIVE, Status.SOLD)))
                .thenReturn(Optional.of(product));

        when(likeRepository.deleteLike(user.getId(), product.getId()))
                .thenReturn(1);

        likeService.unlikeProduct(product.getId(), authentication);

        verify(currentUserProvider).get(authentication);
        verify(productRepository).findByIdAndStatusIn(product.getId(), List.of(Status.ACTIVE, Status.SOLD));
        verify(likeRepository).deleteLike(user.getId(), product.getId());
    }

    @Test
    void shouldUnlikeSoldProduct() {
        product.setStatus(Status.SOLD);

        when(currentUserProvider.get(authentication))
                .thenReturn(user);

        when(productRepository.findByIdAndStatusIn(product.getId(), List.of(Status.ACTIVE, Status.SOLD)))
                .thenReturn(Optional.of(product));

        when(likeRepository.deleteLike(user.getId(), product.getId()))
                .thenReturn(1);

        likeService.unlikeProduct(product.getId(), authentication);

        verify(currentUserProvider).get(authentication);
        verify(productRepository).findByIdAndStatusIn(product.getId(), List.of(Status.ACTIVE, Status.SOLD));
        verify(likeRepository).deleteLike(user.getId(), product.getId());
    }
}
