package com.example.ecommerce.mappers.brand;

import com.example.ecommerce.dtos.brand.BrandResponse;
import com.example.ecommerce.entitites.product.productEntities.Brand;
import org.springframework.stereotype.Component;

@Component
public class BrandMapper {
    public BrandResponse toResponse(Brand brand) {
        return BrandResponse.builder()
                .id(brand.getId())
                .name(brand.getName())
                .build();
    }
}
