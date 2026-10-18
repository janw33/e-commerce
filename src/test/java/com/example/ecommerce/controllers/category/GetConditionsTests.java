package com.example.ecommerce.controllers.category;

import com.example.ecommerce.config.SecurityConfig;
import com.example.ecommerce.entitites.product.enums.Condition;
import com.example.ecommerce.exceptions.user.UserInactiveException;
import com.example.ecommerce.exceptions.user.UserNotFoundException;
import com.example.ecommerce.services.category.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
@Import(SecurityConfig.class)
class GetConditionsTests {
    private static final String ENDPOINT = "/api/v1/category/conditions";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    private final JwtAuthenticationToken authentication = new JwtAuthenticationToken(
            Jwt.withTokenValue("test-token")
                    .header("alg", "RS256")
                    .subject("test-user")
                    .build()
    );

    @Test
    void shouldReturnHttp401WhenUserIsUnauthenticated() throws Exception {
        mockMvc.perform(get(ENDPOINT))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(categoryService);
    }

    @Test
    void shouldReturnAllConditionsWhenRequestIsValid() throws Exception {
        when(categoryService.getConditions(authentication))
                .thenReturn(List.of(Condition.values()));

        mockMvc.perform(get(ENDPOINT).with(authentication(authentication)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$", contains(
                        "NEW", "LIKE_NEW", "VERY_GOOD", "GOOD", "SATISFACTORY"
                )));

        verify(categoryService).getConditions(authentication);
    }

    @Test
    void shouldReturnHttp404WhenUserIsNotFound() throws Exception {
        when(categoryService.getConditions(authentication))
                .thenThrow(new UserNotFoundException());

        mockMvc.perform(get(ENDPOINT).with(authentication(authentication)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("USER_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("User not found"))
                .andExpect(jsonPath("$.path").value(ENDPOINT));
    }

    @Test
    void shouldReturnHttp403WhenUserIsInactive() throws Exception {
        when(categoryService.getConditions(authentication))
                .thenThrow(new UserInactiveException());

        mockMvc.perform(get(ENDPOINT).with(authentication(authentication)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("USER_INACTIVE"))
                .andExpect(jsonPath("$.path").value(ENDPOINT));
    }
}
