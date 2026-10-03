package com.example.ecommerce;

import com.example.ecommerce.dtos.auth.UserResponse;
import com.example.ecommerce.dtos.category.CategoryResponse;
import com.example.ecommerce.dtos.category.CategoryWithChildrenProjection;
import com.example.ecommerce.entitites.product.enums.SizeType;
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

    public static Category category() {
        return Category.builder()
                .id(1L)
                .name("test")
                .parent(null)
                .sizeType(SizeType.NONE)
                .build();
    }

    public static CategoryResponse categoryResponse() {
        return CategoryResponse.builder()
                .id(category().getId())
                .name(category().getName())
                .sizeType(category().getSizeType())
                .hasChildren(true)
                .build();
    }
}
