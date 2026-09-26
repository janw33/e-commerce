package services;

import entitites.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import repositories.UserRepository;

@Service
public class UserProvisioningService {

    private final UserRepository userRepository;

    public UserProvisioningService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getOrCreate(Authentication authentication) {
        String externalId = authentication.getName();

        return userRepository.findByExternalId(externalId)
                .orElseGet(() -> createUser(authentication));
    }

    private User createUser(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();

        User user = User.builder()
                .externalId(jwt.getSubject())
                .email(jwt.getClaimAsString("email"))
                .firstName(jwt.getClaimAsString("given_name"))
                .lastName(jwt.getClaimAsString("family_name"))
                .phoneNumber(jwt.getClaimAsString("phone_number"))
                .build();

        return userRepository.save(user);
    }
}
