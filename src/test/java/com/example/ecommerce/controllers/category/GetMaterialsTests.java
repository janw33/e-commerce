package com.example.ecommerce.controllers.category;

import com.example.ecommerce.BaseTest.category.BaseTestCategory;
import com.example.ecommerce.entitites.product.enums.Material;
import com.example.ecommerce.exceptions.user.UserNotFoundException;
import com.example.ecommerce.services.category.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class GetMaterialsTests extends BaseTestCategory {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

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
                MockMvcRequestBuilders.get("/api/v1/category/materials")
                        .with(authenticatedUser())
        );

        for (ResultMatcher matcher : matchers) {
            result.andExpect(matcher);
        }
    }

    @Test
    void shouldReturnHttp401WhenUserIsUnauthenticated() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/category/materials"))
                .andExpect(
                        status().isUnauthorized()
                );
    }

    @Test
    void shouldReturnHttp404WhenUserIsNotFound() throws Exception {
        when(categoryService.getMaterials(any(Authentication.class)))
                .thenThrow(new UserNotFoundException());

        performGet(
                status().isNotFound(),
                jsonPath("$.error").value("USER_NOT_FOUND"),
                jsonPath("$.message").value("User not found"));
    }

    @Test
    void shouldReturnHttp200WhenRequestIsValid() throws Exception {
        List<Material> materials = List.of(Material.values());

        when(categoryService.getMaterials(any(Authentication.class)))
                .thenReturn(materials);

        performGet(
                status().isOk(),
                jsonPath("$").isArray(),
                jsonPath("$").value(contains(
                        materials.stream()
                                .map(Material::name)
                                .toArray(String[]::new)
                )));

        verify(categoryService).getMaterials(any(Authentication.class));
    }
}
