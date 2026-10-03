package com.example.ecommerce.security;

import com.example.ecommerce.BaseTest.category.BaseTestCategory;
import com.example.ecommerce.BaseTest.user.BaseTestUser;
import com.example.ecommerce.config.SecurityConfig;
import com.example.ecommerce.controllers.auth.AuthController;
import com.example.ecommerce.dtos.auth.UserResponse;
import com.example.ecommerce.mappers.user.UserMapper;
import com.example.ecommerce.providers.user.CurrentUserProvider;
import com.example.ecommerce.services.category.CategoryService;
import com.example.ecommerce.services.user.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest extends BaseTestCategory {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;


    @Test
    void shouldReturn401WhenUserIsUnauthenticated() throws Exception {
        mockMvc.perform(
                        get("/api/v1/category")
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAllowRequestWhenUserIsAuthenticated() throws Exception {
        when(categoryService.getCategory(any(), any()))
               .thenReturn(categoryDetailsResponse);

        mockMvc.perform(
                get("/api/v1/category/1")
                        .with(jwt())
        ).andExpect(
                status().isOk()
        );
    }
}
