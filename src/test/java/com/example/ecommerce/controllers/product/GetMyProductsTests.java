package com.example.ecommerce.controllers.product;

import com.example.ecommerce.BaseTest.product.BaseTestProduct;
import com.example.ecommerce.dtos.product.MyProductSummaryResponse;
import com.example.ecommerce.entitites.product.enums.Status;
import com.example.ecommerce.exceptions.product.InvalidStatusException;
import com.example.ecommerce.exceptions.user.UserNotFoundException;
import com.example.ecommerce.services.product.ProductService;
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
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class GetMyProductsTests extends BaseTestProduct {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

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
                MockMvcRequestBuilders.get("/api/v1/products/my")
                        .with(authenticatedUser())
        );

        for (ResultMatcher matcher : matchers) {
            result.andExpect(matcher);
        }
    }

    @Test
    void shouldReturnHttp401WhenUserIsUnauthenticated() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/products/my"))
                .andExpect(
                        status().isUnauthorized()
                );
    }

    @Test
    void shouldReturnHttp400WhenStatusIsInvalid() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/products/my?status=invalid")
                        .with(authenticatedUser()))
                .andExpect(
                        status().isBadRequest()
                );
    }

    @Test
    void shouldReturnHttp404WhenUserIsNotFound() throws Exception {
        when(productService.getMyProducts(any(), any(), any()))
                .thenThrow(new UserNotFoundException());

        performGet(
                status().isNotFound(),
                jsonPath("$.error").value("USER_NOT_FOUND"),
                jsonPath("$.message").value("User not found")
        );
    }

    @Test
    void shouldReturnHttp404WhenStatusIsInvalid() throws Exception {
        when(productService.getMyProducts(any(), any(), any()))
                .thenThrow(new InvalidStatusException());

        performGet(
                status().isBadRequest(),
                jsonPath("$.error").value("INVALID_STATUS"),
                jsonPath("$.message").value("Invalid status for this operation")
        );
    }

    @Test
    void shouldReturnHttp200WhenRequestIsValid() throws Exception {
        MyProductSummaryResponse response = MyProductSummaryResponse.builder()
                .id(product.getId())
                .imageUrl(image.getUrl())
                .likeCount(5L)
                .viewCount(10L)
                .build();

        Page<MyProductSummaryResponse> page = new PageImpl<>(List.of(response));

        when(productService.getMyProducts(any(), any(), any()))
                .thenReturn(page);

        performGet(
                status().isOk(),
                jsonPath("$.content").isArray(),
                jsonPath("$.totalElements").value(1),
                jsonPath("$.content[0].id").value(product.getId()),
                jsonPath("$.content[0].imageUrl").value(image.getUrl()),
                jsonPath("$.content[0].likeCount").value(5),
                jsonPath("$.content[0].viewCount").value(10)
        );

        verify(productService).getMyProducts(any(), eq(Status.ACTIVE), any());
    }
}
