package com.example.ecommerce.services.user;

import com.example.ecommerce.dtos.auth.UserResponse;
import com.example.ecommerce.entitites.user.User;
import com.example.ecommerce.mappers.user.UserMapper;
import com.example.ecommerce.providers.user.CurrentUserProvider;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final CurrentUserProvider currentUserProvider;
    private final UserMapper userMapper;

    public AuthService(CurrentUserProvider currentUserProvider, UserMapper userMapper) {
        this.currentUserProvider = currentUserProvider;
        this.userMapper = userMapper;
    }

    public UserResponse getCurrentUser(Authentication authentication) {
        User user = currentUserProvider.get(authentication);
        return userMapper.toResponse(user);
    }
}
