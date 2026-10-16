package com.example.ecommerce.mappers.image;

import com.example.ecommerce.dtos.image.CreateImageRequest;
import com.example.ecommerce.dtos.image.ImageResponse;
import com.example.ecommerce.entitites.product.Product;
import com.example.ecommerce.entitites.product.productEntities.Image;
import org.springframework.stereotype.Component;

@Component
public class ImageMapper {
    public ImageResponse toResponse(Image image) {
        return new ImageResponse(image.getId(), image.getUrl());
    }

    public Image toEntity(CreateImageRequest createImageRequest, Product product, int position) {
        return Image.builder()
                .product(product)
                .url(createImageRequest.getUrl())
                .position(position)
                .build();
    }
}
