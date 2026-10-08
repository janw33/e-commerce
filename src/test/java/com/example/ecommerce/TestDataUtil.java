package com.example.ecommerce;

import com.example.ecommerce.dtos.auth.UserResponse;
import com.example.ecommerce.dtos.brand.BrandResponse;
import com.example.ecommerce.dtos.category.CategoryDetailsResponse;
import com.example.ecommerce.dtos.category.CategorySummaryResponse;
import com.example.ecommerce.dtos.image.CreateImageRequest;
import com.example.ecommerce.dtos.image.ImageResponse;
import com.example.ecommerce.dtos.product.CreateProductRequest;
import com.example.ecommerce.entitites.product.Product;
import com.example.ecommerce.entitites.product.enums.*;
import com.example.ecommerce.entitites.product.productEntities.Brand;
import com.example.ecommerce.entitites.product.productEntities.Category;
import com.example.ecommerce.entitites.product.productEntities.Image;
import com.example.ecommerce.entitites.user.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public final class TestDataUtil {
    private static LocalDateTime testDate() {
        return LocalDateTime.of(1, 1, 1, 1, 1);
    }

    public static User user() {
        return User.builder()
                .id(1L)
                .externalId("1")
                .firstName("test")
                .lastName("test")
                .email("test@email.com")
                .phoneNumber("111-111-111")
                .active(true)
                .createdAt(testDate())
                .updatedAt(testDate())
                .build();
    }

    public static UserResponse userResponse() {
        return UserResponse.builder()
                .id(user().getId())
                .externalId(user().getExternalId())
                .firstName(user().getFirstName())
                .lastName(user().getLastName())
                .email(user().getEmail())
                .phoneNumber(user().getPhoneNumber())
                .build();
    }

    public static Category categoryParent() {
        return Category.builder()
                .id(null)
                .name("testParent")
                .parent(null)
                .sizeType(SizeType.NONE)
                .build();
    }


    public static Category category() {
        return Category.builder()
                .id(1L)
                .name("test")
                .parent(categoryParent())
                .sizeType(SizeType.NONE)
                .build();
    }

    public static CategorySummaryResponse categorySummaryResponse() {
        return CategorySummaryResponse.builder()
                .id(category().getId())
                .name(category().getName())
                .sizeType(category().getSizeType())
                .hasChildren(true)
                .build();
    }

    public static CategoryDetailsResponse categoryDetailsResponse() {
        return CategoryDetailsResponse.builder()
                .id(category().getId())
                .name(category().getName())
                .sizeType(category().getSizeType())
                .parentId(category().getParent().getId())
                .hasChildren(true)
                .build();
    }

    public static Brand brand() {
        return Brand.builder()
                .id(1L)
                .name("test")
                .build();
    }

    public static BrandResponse brandResponse() {
        return BrandResponse.builder()
                .id(brand().getId())
                .name(brand().getName())
                .build();
    }

    public static Image image() {
        return Image.builder()
                .id(1L)
                .product(product())
                .url("test")
                .position(0)
                .build();
    }

    public static CreateImageRequest createImageRequest() {
        return new CreateImageRequest(image().getUrl());
    }

    public static ImageResponse imageResponse() {return new ImageResponse(image().getUrl());}

    public static Product product() {
        return Product.builder()
                .id(1L)
                .seller(user())
                .category(category())
                .brand(brand())
                .title("test")
                .description("test")
                .price(BigDecimal.TEN)
                .currency(Currency.EUR)
                .colors(Set.of(Color.BLUE))
                .condition(Condition.NEW)
                .materials(Set.of(Material.COTTON))
                .size(Size.ONE_SIZE)
                .shippingSize(ShippingSize.MEDIUM)
                .viewCount(0L)
                .status(Status.ACTIVE)
                .createdAt(testDate())
                .updatedAt(testDate())
                .deletedAt(null)
                .soldAt(null)
                .build();
    }

    public static CreateProductRequest createProductRequest() {
        return CreateProductRequest.builder()
                .images(List.of(createImageRequest()))
                .title(product().getTitle())
                .description(product().getDescription())
                .categoryId(category().getId())
                .brandId(brand().getId())
                .size(product().getSize())
                .shippingSize(product().getShippingSize())
                .condition(product().getCondition())
                .colors(product().getColors())
                .materials(product().getMaterials())
                .price(product().getPrice())
                .currency(product().getCurrency())
                .build();
    }
}
