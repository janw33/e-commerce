package com.example.ecommerce.controllers.auth;

import com.example.ecommerce.dtos.auth.UserResponse;
import com.example.ecommerce.services.user.AuthService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/me")
    public UserResponse getCurrentUser(Authentication authentication) {
        return authService.getCurrentUser(authentication);
    }
}