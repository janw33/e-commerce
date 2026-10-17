package com.example.ecommerce.controllers.category;

import com.example.ecommerce.BaseTest.category.BaseTestCategory;
import com.example.ecommerce.entitites.product.enums.Size;
import com.example.ecommerce.exceptions.category.CategoryNotFoundException;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class GetSizesTests extends BaseTestCategory {
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

    private void performGet(Long categoryId, ResultMatcher... matchers) throws Exception {
        var request =
                MockMvcRequestBuilders.get("/api/v1/category/sizes")
                .with(authenticatedUser());

        if (categoryId != null) {
            request.param("categoryId", categoryId.toString());
        }

        var result = mockMvc.perform(request);

        for (ResultMatcher matcher : matchers) {
            result.andExpect(matcher);
        }
    }

    @Test
    void shouldReturnHttp401WhenUserIsUnauthenticated() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/category/sizes"))
                .andExpect(
                        status().isUnauthorized()
                );
    }

    @Test
    void shouldReturnHttp400WhenCategoryIdIsInvalid() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/category/sizes")
                        .param("categoryId", "invalid")
                        .with(authenticatedUser()))
                .andExpect(
                        status().isBadRequest()
                );
    }

    @Test
    void shouldReturnHttp404WhenUserIsNotFound() throws Exception {
        when(categoryService.getSizes(any(), any(Authentication.class)))
                .thenThrow(new UserNotFoundException());

        performGet(null,
                status().isNotFound(),
                jsonPath("$.error").value("USER_NOT_FOUND"),
                jsonPath("$.message").value("User not found"));
    }

    @Test
    void shouldReturnHttp404WhenCategoryIsNotFound() throws Exception {
        when(categoryService.getSizes(eq(category.getId()), any(Authentication.class)))
                .thenThrow(new CategoryNotFoundException());

        performGet(category.getId(),
                status().isNotFound(),
                jsonPath("$.error").value("CATEGORY_NOT_FOUND"),
                jsonPath("$.message").value("Category not found"));
    }

    @Test
    void shouldReturnHttp200WhenRequestIsValid() throws Exception {
        List<Size> sizes = List.of(Size.values());
        when(categoryService.getSizes(eq(null), any(Authentication.class)))
                .thenReturn(sizes);

        performGet(null,
                status().isOk(),
                jsonPath("$").isArray(),
                jsonPath("$.length()").value(sizes.size()),
                jsonPath("$[0]").value(sizes.getFirst().name()));

        verify(categoryService).getSizes(eq(null), any(Authentication.class));
    }

    @Test
    void shouldReturnHttp200WithCategorySizesWhenCategoryIdIsProvided() throws Exception {
        List<Size> sizes = List.of(Size.ONE_SIZE);

        when(categoryService.getSizes(eq(category.getId()), any(Authentication.class)))
                .thenReturn(sizes);

        performGet(category.getId(),
                status().isOk(),
                jsonPath("$").isArray(),
                jsonPath("$.length()").value(1),
                jsonPath("$[0]").value(Size.ONE_SIZE.name()));

        verify(categoryService).getSizes(eq(category.getId()), any(Authentication.class));
    }
}
