package com.example.ecommerce.controllers.auth;

import com.example.ecommerce.providers.user.CurrentUserProvider;
import com.example.ecommerce.dtos.auth.UserResponse;
import com.example.ecommerce.entitites.user.User;
import com.example.ecommerce.mappers.user.UserMapper;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auth")
public class AuthController {
    private final CurrentUserProvider currentUserProvider;
    private final UserMapper userMapper;

    public AuthController(CurrentUserProvider currentUserProvider, UserMapper userMapper) {
        this.currentUserProvider = currentUserProvider;
        this.userMapper = userMapper;
    }

    @GetMapping("/me")
    public UserResponse getCurrentUser(Authentication authentication) {
        User user = currentUserProvider.get(authentication);
        return userMapper.toResponse(user);
    }
}