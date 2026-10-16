package com.example.ecommerce.controllers.image;

import com.example.ecommerce.BaseTest.product.BaseTestProduct;
import com.example.ecommerce.exceptions.product.ProductNotFoundException;
import com.example.ecommerce.exceptions.user.UserNotFoundException;
import com.example.ecommerce.services.image.ImageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class GetImagesTests extends BaseTestProduct {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ImageService imageService;

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
                MockMvcRequestBuilders.get("/api/v1/image/{productId}", product.getId())
                        .with(authenticatedUser())
        );

        for (ResultMatcher matcher : matchers) {
            result.andExpect(matcher);
        }
    }

    @Test
    void shouldReturnHttp401WhenUserIsUnauthenticated() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/image/{productId}", product.getId()))
                .andExpect(
                        status().isUnauthorized()
                );

        verifyNoInteractions(imageService);
    }

    @Test
    void shouldReturnHttp404WhenUserIsNotFound() throws Exception {
        when(imageService.getImages(any(), eq(product.getId())))
                .thenThrow(new UserNotFoundException());

        performGet(
                status().isNotFound(),
                jsonPath("$.error").value("USER_NOT_FOUND"),
                jsonPath("$.message").value("User not found")
        );
    }

    @Test
    void shouldReturnHttp404WhenProductIsNotFound() throws Exception {
        when(imageService.getImages(any(), eq(product.getId())))
                .thenThrow(new ProductNotFoundException());

        performGet(
                status().isNotFound(),
                jsonPath("$.error").value("PRODUCT_NOT_FOUND"),
                jsonPath("$.message").value("Product not found")
        );
    }

    @Test
    void shouldReturnHttp200WithImagesWhenRequestIsValid() throws Exception {
        when(imageService.getImages(any(), eq(product.getId())))
                .thenReturn(List.of(imageResponse));

        performGet(
                status().isOk(),
                jsonPath("$").isArray(),
                jsonPath("$.length()").value(1),
                jsonPath("$[0].id").value(image.getId()),
                jsonPath("$[0].url").value(image.getUrl())
        );

        verify(imageService).getImages(any(), eq(product.getId()));
    }
}
