package com.example.ecommerce.services.user;

import com.example.ecommerce.entitites.user.User;
import com.example.ecommerce.exceptions.user.UserNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import com.example.ecommerce.repositories.user.UserRepository;

@Service
public class UserProvisioningService {

    private final UserRepository userRepository;

    public UserProvisioningService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User get(Authentication authentication) {
        String externalId = authentication.getName();

        return userRepository.findByExternalIdAndActiveTrue(externalId)
                .orElseThrow(UserNotFoundException::new);
    }

    public User getOrCreate(Authentication authentication) {
        String externalId = authentication.getName();

        return userRepository.findByExternalIdAndActiveTrue(externalId)
                .orElseGet(() -> createUser(authentication));
    }

    private User createUser(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();

        User user = User.builder()
                .externalId(jwt.getSubject())
                .firstName(jwt.getClaimAsString("given_name"))
                .lastName(jwt.getClaimAsString("family_name"))
                .email(jwt.getClaimAsString("email"))
                .phoneNumber(jwt.getClaimAsString("phone_number"))
                .build();

        return userRepository.save(user);
    }
}
