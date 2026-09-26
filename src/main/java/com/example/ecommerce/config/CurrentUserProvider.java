package config;

import entitites.User;
import exceptions.UserNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import repositories.UserRepository;

@Component
public class CurrentUserProvider {

    private final UserRepository userRepository;

    public CurrentUserProvider(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User get(Authentication authentication) {
        String externalId = authentication.getName();

        return userRepository.findByExternalId(externalId)
                .orElseThrow(UserNotFoundException::new);
    }
}
