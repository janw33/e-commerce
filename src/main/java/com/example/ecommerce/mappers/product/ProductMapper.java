package com.example.ecommerce.mappers.product;

import com.example.ecommerce.dtos.image.ImageResponse;
import com.example.ecommerce.dtos.product.CreateProductRequest;
import com.example.ecommerce.dtos.product.ProductResponse;
import com.example.ecommerce.entitites.product.Product;
import com.example.ecommerce.entitites.product.productEntities.Brand;
import com.example.ecommerce.entitites.product.productEntities.Category;
import com.example.ecommerce.entitites.user.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductMapper {
    public Product toEntity(CreateProductRequest request, User seller, Category category, Brand brand) {
        return Product.builder()
                .seller(seller)
                .category(category)
                .brand(brand)
                .title(request.getTitle())
                .description(request.getDescription())
                .price(request.getPrice())
                .currency(request.getCurrency())
                .colors(request.getColors())
                .condition(request.getCondition())
                .materials(request.getMaterials())
                .size(request.getSize())
                .shippingSize(request.getShippingSize())
                .build();
    }

    public ProductResponse toResponse(Product product, long likeCount, List<ImageResponse> images) {
        User seller = product.getSeller();
        Brand brand = product.getBrand();

        return ProductResponse.builder()
                .id(product.getId())
                .sellerId(seller.getId())
                .sellerUsername(seller.getFirstName())
                .categoryId(product.getCategory().getId())
                .brandId(brand.getId())
                .brandName(brand.getName()) //
                .title(product.getTitle())
                .description(product.getDescription())
                .price(product.getPrice()) //
                .currency(product.getCurrency())
                .colors(product.getColors())
                .condition(product.getCondition()) //
                .materials(product.getMaterials())
                .size(product.getSize())
                .shippingSize(product.getShippingSize())
                .viewCount(product.getViewCount())
                .likeCount(likeCount)
                .status(product.getStatus())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .deletedAt(product.getDeletedAt())
                .soldAt(product.getSoldAt())
                .images(images)
                .build();
    }
}
