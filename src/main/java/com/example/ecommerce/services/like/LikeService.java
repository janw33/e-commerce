package com.example.ecommerce.services.like;

import com.example.ecommerce.entitites.product.Product;
import com.example.ecommerce.entitites.product.enums.Status;
import com.example.ecommerce.entitites.product.productEntities.Like;
import com.example.ecommerce.entitites.user.User;
import com.example.ecommerce.exceptions.like.LikeAlreadyExistsException;
import com.example.ecommerce.exceptions.like.LikeNotFoundException;
import com.example.ecommerce.exceptions.product.ProductNotFoundException;
import com.example.ecommerce.providers.CurrentUserProvider;
import com.example.ecommerce.repository.like.LikeRepository;
import com.example.ecommerce.repository.product.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LikeService {
    private final LikeRepository likeRepository;
    private final ProductRepository productRepository;
    private final CurrentUserProvider currentUserProvider;

    public LikeService(LikeRepository likeRepository, ProductRepository productRepository, CurrentUserProvider currentUserProvider) {
        this.likeRepository = likeRepository;
        this.productRepository = productRepository;
        this.currentUserProvider = currentUserProvider;
    }

    @Transactional
    public void likeProduct(Long productId, Authentication authentication) {
        User currentUser = currentUserProvider.get(authentication);

        productRepository.findByIdAndStatus(productId, Status.ACTIVE)
                .orElseThrow(ProductNotFoundException::new);

        int insertedRows = likeRepository.insertIfAbsent(currentUser.getId(), productId);

        if (insertedRows == 0) {
            throw new LikeAlreadyExistsException();
        }
    }

    @Transactional
    public void unlikeProduct(Long productId, Authentication authentication) {
        User currentUser = currentUserProvider.get(authentication);

        productRepository.findByIdAndStatusIn(productId, List.of(Status.ACTIVE, Status.SOLD))
                .orElseThrow(ProductNotFoundException::new);

        int deletedRows = likeRepository.deleteLike(currentUser.getId(), productId);

        if (deletedRows == 0) {
            throw new LikeNotFoundException();
        }
    }
}
