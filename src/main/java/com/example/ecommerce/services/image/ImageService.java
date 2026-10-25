package com.example.ecommerce.services.image;

import com.example.ecommerce.dtos.image.ImageResponse;
import com.example.ecommerce.entitites.product.Product;
import com.example.ecommerce.entitites.product.enums.Status;
import com.example.ecommerce.entitites.user.User;
import com.example.ecommerce.exceptions.image.ImageNotFoundException;
import com.example.ecommerce.exceptions.product.ProductNotFoundException;
import com.example.ecommerce.mappers.image.ImageMapper;
import com.example.ecommerce.providers.CurrentUserProvider;
import com.example.ecommerce.repository.image.ImageRepository;
import com.example.ecommerce.repository.product.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImageService {
    private final CurrentUserProvider currentUserProvider;
    private final ProductRepository productRepository;
    private final ImageRepository imageRepository;
    private final ImageMapper imageMapper;

    public ImageService(CurrentUserProvider currentUserProvider, ProductRepository productRepository, ImageRepository imageRepository, ImageMapper imageMapper) {
        this.currentUserProvider = currentUserProvider;
        this.productRepository = productRepository;
        this.imageRepository = imageRepository;
        this.imageMapper = imageMapper;
    }

    public List<ImageResponse> getImages(Authentication authentication, Long productId) {
        currentUserProvider.get(authentication);

        Product product = productRepository.findByIdAndStatusIn(productId, List.of(Status.ACTIVE, Status.SOLD))
                        .orElseThrow(ProductNotFoundException::new);

        return imageRepository.findAllByProductIdOrderByPositionAsc(product.getId())
                .stream()
                .map(imageMapper::toResponse)
                .toList();
    }
}
