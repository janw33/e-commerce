package com.example.ecommerce.services.like;

import com.example.ecommerce.BaseTest.product.BaseTestProduct;
import com.example.ecommerce.entitites.product.enums.Status;
import com.example.ecommerce.exceptions.like.LikeAlreadyExistsException;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LikeProductTests extends BaseTestProduct {
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
        likeService = new LikeService(likeRepository, productRepository, currentUserProvider);
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenUserDoesntExists() {
        when(currentUserProvider.get(authentication))
                .thenThrow(new UserNotFoundException());

        assertThrows(
                UserNotFoundException.class,
                () -> likeService.likeProduct(product.getId(), authentication)
        );

        verifyNoInteractions(productRepository, likeRepository);
    }

    @Test
    void shouldThrowProductNotFoundExceptionWhenActiveProductDoesntExists() {
        when(currentUserProvider.get(authentication))
                .thenReturn(user);

        when(productRepository.findByIdAndStatus(product.getId(), Status.ACTIVE))
                .thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> likeService.likeProduct(product.getId(), authentication)
        );

        verifyNoInteractions(likeRepository);
    }

    @Test
    void shouldThrowLikeAlreadyExistsExceptionWhenProductIsAlreadyLiked() {
        when(currentUserProvider.get(authentication))
                .thenReturn(user);

        when(productRepository.findByIdAndStatus(product.getId(), Status.ACTIVE))
                .thenReturn(Optional.of(product));

        when(likeRepository.insertIfAbsent(user.getId(), product.getId()))
                .thenReturn(0);

        assertThrows(
                LikeAlreadyExistsException.class,
                () -> likeService.likeProduct(product.getId(), authentication)
        );

        verify(likeRepository).insertIfAbsent(user.getId(), product.getId());
    }

    @Test
    void shouldLikeActiveProduct() {
        when(currentUserProvider.get(authentication))
                .thenReturn(user);

        when(productRepository.findByIdAndStatus(product.getId(), Status.ACTIVE))
                .thenReturn(Optional.of(product));

        when(likeRepository.insertIfAbsent(user.getId(), product.getId()))
                .thenReturn(1);

        likeService.likeProduct(product.getId(), authentication);

        verify(currentUserProvider).get(authentication);
        verify(productRepository).findByIdAndStatus(product.getId(), Status.ACTIVE);
        verify(likeRepository).insertIfAbsent(user.getId(), product.getId());
    }
}
