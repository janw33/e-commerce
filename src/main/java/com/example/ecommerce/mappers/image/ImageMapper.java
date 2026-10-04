package com.example.ecommerce.mappers.image;

import com.example.ecommerce.dtos.image.ImageResponse;
import com.example.ecommerce.entitites.product.productEntities.Image;
import org.springframework.stereotype.Component;

@Component
public class ImageMapper {
    public ImageResponse toResponse(Image image) {
        return new ImageResponse(image.getUrl());
    }
}
