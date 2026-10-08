package com.example.ecommerce;

import com.example.ecommerce.dtos.auth.UserResponse;
import com.example.ecommerce.dtos.brand.BrandResponse;
import com.example.ecommerce.dtos.category.CategoryDetailsResponse;
import com.example.ecommerce.dtos.category.CategorySummaryResponse;
import com.example.ecommerce.entitites.product.Product;
import com.example.ecommerce.entitites.product.enums.SizeType;
import com.example.ecommerce.entitites.product.productEntities.Brand;
import com.example.ecommerce.entitites.product.productEntities.Category;
import com.example.ecommerce.entitites.user.User;

import java.time.LocalDateTime;

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
}
