package com.example.ecommerce.services.image;

import com.example.ecommerce.dtos.image.ImageResponse;
import com.example.ecommerce.mappers.image.ImageMapper;
import com.example.ecommerce.providers.CurrentUserProvider;
import com.example.ecommerce.repository.image.ImageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImageService {
    private final CurrentUserProvider currentUserProvider;
    private final ImageRepository imageRepository;
    private final ImageMapper imageMapper;

    public ImageService(CurrentUserProvider currentUserProvider, ImageRepository imageRepository, ImageMapper imageMapper) {
        this.currentUserProvider = currentUserProvider;
        this.imageRepository = imageRepository;
        this.imageMapper = imageMapper;
    }

    public List<ImageResponse> getImages(Authentication authentication, Long productId) {
        currentUserProvider.get(authentication);

        return imageRepository.findAllByProductIdOrderByPositionAsc(productId)
                .stream()
                .map(imageMapper::toResponse)
                .toList();
    }
}
