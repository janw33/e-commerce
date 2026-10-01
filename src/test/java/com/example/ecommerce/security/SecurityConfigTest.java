package com.example.ecommerce.security;

import com.example.ecommerce.BaseTest.user.BaseTestUser;
import com.example.ecommerce.config.SecurityConfig;
import com.example.ecommerce.controllers.auth.AuthController;
import com.example.ecommerce.dtos.auth.UserResponse;
import com.example.ecommerce.mappers.user.UserMapper;
import com.example.ecommerce.providers.user.CurrentUserProvider;
import com.example.ecommerce.services.user.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@Import(SecurityConfig.class)
class SecurityConfigTest extends BaseTestUser {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Test
    void shouldReturn401WhenUserIsUnauthenticated() throws Exception {
        mockMvc.perform(
                get("/api/v1/auth/me")
        ).andExpect(
                status().isUnauthorized()
        );
    }

    @Test
    void shouldAllowRequestWhenUserIsAuthenticated() throws Exception {
        when(authService.getCurrentUser(any()))
                .thenReturn(userResponse);

        mockMvc.perform(
                get("/api/v1/auth/me")
                        .with(jwt())
        ).andExpect(
                status().isOk()
        );
    }
}
