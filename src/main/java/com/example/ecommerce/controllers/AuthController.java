package controllers;

import config.CurrentUserProvider;
import dtos.AuthResponse;
import dtos.RegisterRequest;
import dtos.UserResponse;
import entitites.User;
import jakarta.validation.Valid;
import mappers.UserMapper;
import org.springframework.http.HttpStatus;
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