package com.example.ecommerce.controllers.category;

import com.example.ecommerce.BaseTest.category.BaseTestCategory;
import com.example.ecommerce.exceptions.category.CategoryNotFoundException;
import com.example.ecommerce.exceptions.user.UserNotFoundException;
import com.example.ecommerce.services.category.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class GetCategoryTests extends BaseTestCategory {
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

    private void performGet(Long id, ResultMatcher... matchers) throws Exception {
        var result = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/category/" + id)
                        .with(authenticatedUser())
        );

        for (ResultMatcher matcher : matchers) {
            result.andExpect(matcher);
        }
    }

    @Test
    void shouldReturnHttp401WhenUserIsUnauthenticated() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/category/1"))
                .andExpect(
                        status().isUnauthorized()
                );
    }

    @Test
    void shouldReturnHttp404WhenUserIsNotFound() throws Exception {
        when(categoryService.getCategory(any(Authentication.class), eq(category.getId())))
                .thenThrow(new UserNotFoundException());

        performGet(
                category.getId(),
                status().isNotFound(),
                jsonPath("$.error").value("USER_NOT_FOUND"),
                jsonPath("$.message").value("User not found"));
    }

    @Test
    void shouldReturnHttp404WhenCategoryIsNotFound() throws Exception {
        when(categoryService.getCategory(any(Authentication.class), any()))
                .thenThrow(new CategoryNotFoundException());

        performGet(
                category.getId(),
                status().isNotFound(),
                jsonPath("$.error").value("CATEGORY_NOT_FOUND"),
                jsonPath("$.message").value("Category not found"));
    }

    @Test
    void shouldReturnHttp200WhenRequestIsValid() throws Exception {
        when(categoryService.getCategory(any(Authentication.class), any()))
                .thenReturn(categoryDetailsResponse);

        performGet(
                category.getId(),
                status().isOk(),
                jsonPath("$.id").value(category.getId()),
                jsonPath("$.name").value(category.getName()),
                jsonPath("$.sizeType").value(category.getSizeType().toString()),
                jsonPath("$.parentId").value(category.getParent().getId()),
                jsonPath("$.hasChildren").value(true));
    }
}
