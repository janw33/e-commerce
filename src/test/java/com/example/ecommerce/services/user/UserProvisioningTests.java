package com.example.ecommerce.services.user;

import com.example.ecommerce.BaseTest.user.BaseTestUser;
import com.example.ecommerce.entitites.user.User;
import com.example.ecommerce.exceptions.user.UserNotFoundException;
import com.example.ecommerce.repository.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserProvisioningTests extends BaseTestUser {
    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserProvisioningService userProvisioningService;

    @Test
    void shouldThrowUserInactiveException() {
        user.setActive(false);

        when(authentication.getName())
                .thenReturn(user.getExternalId());

        when(userRepository.findByExternalId(user.getExternalId()))
                .thenReturn(Optional.of(user));

        assertThrows(
                UserNotFoundException.class,
                () -> userProvisioningService.getOrCreate(authentication)
        );

        verify(userRepository).findByExternalId(user.getExternalId());
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldGetUserWhenUserExists() {
        when(authentication.getName())
                .thenReturn(user.getExternalId());

        when(userRepository.findByExternalId(user.getExternalId()))
                .thenReturn(Optional.of(user));

        User result = userProvisioningService.getOrCreate(authentication);

        assertEquals(user, result);

        verify(userRepository).findByExternalId(user.getExternalId());
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldReturnUserCreatedByAnotherRequest() {
        when(authentication.getName())
                .thenReturn(user.getExternalId());

        when(userRepository.findByExternalId(user.getExternalId()))
                .thenReturn(Optional.empty(), Optional.of(user));

        Jwt jwt = mock(Jwt.class);

        when(authentication.getPrincipal()).thenReturn(jwt);

        when(userRepository.save(any(User.class)))
                .thenThrow(new DataIntegrityViolationException(
                        "Duplicate external id"
                ));

        User result = userProvisioningService.getOrCreate(authentication);

        assertEquals(user, result);

        verify(userRepository, times(2))
                .findByExternalId(user.getExternalId());

        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldCreateUserWhenUserDoesNotExist() {
        Jwt jwt = mock(Jwt.class);

        when(authentication.getName())
                .thenReturn("NotRecordedExternalId");

        when(userRepository.findByExternalId("NotRecordedExternalId"))
                .thenReturn(Optional.empty());

        when(authentication.getPrincipal())
                .thenReturn(jwt);

        when(jwt.getSubject())
                .thenReturn("NotRecordedExternalId");

        when(jwt.getClaimAsString("given_name"))
                .thenReturn(user.getFirstName());

        when(jwt.getClaimAsString("family_name"))
                .thenReturn(user.getLastName());

        when(jwt.getClaimAsString("email"))
                .thenReturn(user.getEmail());

        when(jwt.getClaimAsString("phone_number"))
                .thenReturn(user.getPhoneNumber());

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User result = userProvisioningService.getOrCreate(authentication);

        assertEquals("NotRecordedExternalId", result.getExternalId());
        assertEquals(user.getFirstName(), result.getFirstName());
        assertEquals(user.getLastName(), result.getLastName());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getPhoneNumber(), result.getPhoneNumber());

        verify(userRepository).findByExternalId("NotRecordedExternalId");
        verify(userRepository).save(any(User.class));
    }
}

