package com.example.ecommerce.config;

import com.example.ecommerce.entitites.user.User;
import com.example.ecommerce.services.user.UserProvisioningService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserProvider {

    private final UserProvisioningService userProvisioningService;

    public CurrentUserProvider(UserProvisioningService userProvisioningService) {
        this.userProvisioningService = userProvisioningService;
    }

    public User get(Authentication authentication) {
        return userProvisioningService.getOrCreate(authentication);
    }
}