package com.example.ecommerce.mappers.user;

import com.example.ecommerce.dtos.auth.UserResponse;
import com.example.ecommerce.entitites.user.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .externalId(user.getExternalId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }
}
