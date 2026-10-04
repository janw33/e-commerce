package com.example.ecommerce.providers;

import com.example.ecommerce.BaseTest.user.BaseTestUser;
import com.example.ecommerce.entitites.user.User;
import com.example.ecommerce.services.user.UserProvisioningService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CurrentUserTests extends BaseTestUser {
    @Mock
    private UserProvisioningService userProvisioningService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private CurrentUserProvider currentUserProvider;

    @Test
    void shouldGetUser() {
        when(userProvisioningService.getOrCreate(authentication))
                .thenReturn(user);

        User result = currentUserProvider.getOrCreate(authentication);

        assertEquals(user, result);

        verify(userProvisioningService).getOrCreate(authentication);
    }
}
