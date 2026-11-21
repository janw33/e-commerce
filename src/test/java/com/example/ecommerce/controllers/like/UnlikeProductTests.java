package com.example.ecommerce.controllers.like;

import com.example.ecommerce.BaseTest.product.BaseTestProduct;
import com.example.ecommerce.exceptions.like.LikeNotFoundException;
import com.example.ecommerce.exceptions.product.ProductNotFoundException;
import com.example.ecommerce.exceptions.user.UserNotFoundException;
import com.example.ecommerce.services.like.LikeService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UnlikeProductTests extends BaseTestProduct {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LikeService likeService;

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

    private void performDelete(ResultMatcher... matchers) throws Exception {
        var result = mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/like/{productId}", product.getId())
                        .with(authenticatedUser())
        );

        for (ResultMatcher matcher : matchers) {
            result.andExpect(matcher);
        }
    }

    @Test
    void shouldReturnHttp401WhenUserIsUnauthenticated() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/like/{productId}", product.getId()))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(likeService);
    }

    @Test
    void shouldReturnHttp400WhenProductIdIsInvalid() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/like/{productId}", "invalid")
                        .with(authenticatedUser()))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(likeService);
    }

    @Test
    void shouldReturnHttp404WhenUserIsNotFound() throws Exception {
        doThrow(new UserNotFoundException())
                .when(likeService).unlikeProduct(eq(product.getId()), any());

        performDelete(
                status().isNotFound(),
                jsonPath("$.error").value("USER_NOT_FOUND"),
                jsonPath("$.message").value("User not found")
        );
    }

    @Test
    void shouldReturnHttp404WhenProductIsNotFound() throws Exception {
        doThrow(new ProductNotFoundException())
                .when(likeService).unlikeProduct(eq(product.getId()), any());

        performDelete(
                status().isNotFound(),
                jsonPath("$.error").value("PRODUCT_NOT_FOUND"),
                jsonPath("$.message").value("Product not found")
        );
    }

    @Test
    void shouldReturnHttp404WhenLikeIsNotFound() throws Exception {
        doThrow(new LikeNotFoundException())
                .when(likeService).unlikeProduct(eq(product.getId()), any());

        performDelete(
                status().isNotFound(),
                jsonPath("$.error").value("LIKE_NOT_FOUND"),
                jsonPath("$.message").value("Like not found")
        );
    }

    @Test
    void shouldReturnHttp204WhenRequestIsValid() throws Exception {
        performDelete(status().isNoContent());

        verify(likeService).unlikeProduct(eq(product.getId()), any(Authentication.class));
    }
}
