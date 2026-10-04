package com.example.ecommerce.controllers.brand;

import com.example.ecommerce.BaseTest.brand.BaseTestBrand;
import com.example.ecommerce.BaseTest.category.BaseTestCategory;
import com.example.ecommerce.dtos.brand.BrandResponse;
import com.example.ecommerce.dtos.category.CategorySummaryResponse;
import com.example.ecommerce.exceptions.user.UserNotFoundException;
import com.example.ecommerce.services.brand.BrandService;
import com.example.ecommerce.services.category.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class GetBrandsTests extends BaseTestBrand {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BrandService brandService;

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
        var result = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/brand/" + categoryId)
                        .with(authenticatedUser())
        );

        for (ResultMatcher matcher : matchers) {
            result.andExpect(matcher);
        }
    }

    @Test
    void shouldReturnHttp401WhenUserIsUnauthenticated() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/brand/1"))
                .andExpect(
                        status().isUnauthorized()
                );
    }

    @Test
    void shouldReturnHttp404WhenUserIsNotFound() throws Exception {
        when(brandService.getBrands(any(Authentication.class), eq(category.getId()), any(Pageable.class)))
                .thenThrow(new UserNotFoundException());

        performGet(
                category.getId(),
                status().isNotFound(),
                jsonPath("$.error").value("USER_NOT_FOUND"),
                jsonPath("$.message").value("User not found"));
    }

    @Test
    void shouldReturnHttp200WhenRequestIsValid() throws Exception {
        Page<BrandResponse> page =
                new PageImpl<>(List.of(brandResponse));

        when(brandService.getBrands(any(Authentication.class), eq(category.getId()), any(Pageable.class)))
                .thenReturn(page);

        performGet(
                category.getId(),
                status().isOk(),
                jsonPath("$.content").isArray(),
                jsonPath("$.totalElements").value(1),
                jsonPath("$.content[0].id").value(brand.getId()),
                jsonPath("$.content[0].name").value(brand.getName()));
    }
}
