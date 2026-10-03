package com.example.ecommerce.controllers.user;

import com.example.ecommerce.BaseTest.user.BaseTestUser;
import com.example.ecommerce.services.user.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class GetCurrentUserTests extends BaseTestUser {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    private RequestPostProcessor authenticatedUser() {
        return jwt()
                .jwt(jwt -> jwt
                        .subject(user.getExternalId())
                        .claim("email", user.getEmail())
                        .claim("given_name", user.getFirstName())
                        .claim("family_name", user.getLastName())
                        .claim("phone_number", user.getPhoneNumber())
                );
    }

    private void performGet(ResultMatcher... matchers) throws Exception {
        var result = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/auth/me")
                        .with(authenticatedUser())
        );

        for (ResultMatcher matcher : matchers) {
            result.andExpect(matcher);
        }
    }

    @Test
    void shouldReturnHttp200WhenRequestIsValid() throws Exception {
        when(authService.getCurrentUser(any()))
                .thenReturn(userResponse);

        performGet(status().isOk(),
                jsonPath("$.id").value(user.getId()),
                jsonPath("$.externalId").value(user.getExternalId()),
                jsonPath("$.firstName").value(user.getFirstName()),
                jsonPath("$.lastName").value(user.getLastName()),
                jsonPath("$.email").value(user.getEmail()),
                jsonPath("$.phoneNumber").value(user.getPhoneNumber()));
    }
}
