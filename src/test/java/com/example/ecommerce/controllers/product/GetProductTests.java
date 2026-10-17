package com.example.ecommerce.controllers.product;

import com.example.ecommerce.BaseTest.product.BaseTestProduct;
import com.example.ecommerce.exceptions.product.ProductNotFoundException;
import com.example.ecommerce.exceptions.user.UserNotFoundException;
import com.example.ecommerce.services.product.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class GetProductTests extends BaseTestProduct {
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
                MockMvcRequestBuilders.get("/api/v1/products/{id}", product.getId())
                        .with(authenticatedUser())
        );

        for (ResultMatcher matcher : matchers) {
            result.andExpect(matcher);
        }
    }

    @Test
    void shouldReturnHttp401WhenUserIsUnauthenticated() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/products/{id}", product.getId()))
                .andExpect(
                        status().isUnauthorized()
                );

        verifyNoInteractions(productService);
    }

    @Test
    void shouldReturnHttp404WhenUserIsNotFound() throws Exception {
        when(productService.getProduct(eq(product.getId()), any(Authentication.class)))
                .thenThrow(new UserNotFoundException());

        performGet(
                status().isNotFound(),
                jsonPath("$.error").value("USER_NOT_FOUND"),
                jsonPath("$.message").value("User not found")
        );
    }

    @Test
    void shouldReturnHttp404WhenProductIsNotFound() throws Exception {
        when(productService.getProduct(eq(product.getId()), any(Authentication.class)))
                .thenThrow(new ProductNotFoundException());

        performGet(
                status().isNotFound(),
                jsonPath("$.error").value("PRODUCT_NOT_FOUND"),
                jsonPath("$.message").value("Product not found")
        );
    }

    @Test
    void shouldReturnHttp200WhenProductIsFound() throws Exception {
        when(productService.getProduct(eq(product.getId()), any(Authentication.class)))
                .thenReturn(productResponse);

        performGet(
                status().isOk(),
                jsonPath("$.id").value(product.getId()),
                jsonPath("$.sellerId").value(product.getSeller().getId()),
                jsonPath("$.sellerUsername").value(product.getSeller().getFirstName()),
                jsonPath("$.categoryId").value(product.getCategory().getId()),
                jsonPath("$.brandId").value(product.getBrand().getId()),
                jsonPath("$.brandName").value(product.getBrand().getName()),
                jsonPath("$.title").value(product.getTitle()),
                jsonPath("$.description").value(product.getDescription()),
                jsonPath("$.price").value(product.getPrice()),
                jsonPath("$.condition").value(product.getCondition().name()),
                jsonPath("$.size").value(product.getSize().name()),
                jsonPath("$.shippingSize").value(product.getShippingSize().name()),
                jsonPath("$.viewCount").value(product.getViewCount()),
                jsonPath("$.likeCount").value(productResponse.getLikeCount()),
                jsonPath("$.status").value(product.getStatus().name()),
                jsonPath("$.createdAt").isNotEmpty(),
                jsonPath("$.updatedAt").isNotEmpty(),
                jsonPath("$.deletedAt").value(nullValue()),
                jsonPath("$.soldAt").value(nullValue()),
                jsonPath("$.images").isArray(),
                jsonPath("$.images.length()").value(productResponse.getImages().size()),
                jsonPath("$.images[0].url").value(image.getUrl()),
                jsonPath("$.colors").isArray(),
                jsonPath("$.colors.length()").value(productResponse.getColors().size()),
                jsonPath("$.colors", containsInAnyOrder(
                        productResponse.getColors().stream()
                                .map(Enum::name)
                                .toArray()
                )),
                jsonPath("$.materials").isArray(),
                jsonPath("$.materials.length()").value(productResponse.getMaterials().size()),
                jsonPath("$.materials", containsInAnyOrder(
                        productResponse.getMaterials().stream()
                                .map(Enum::name)
                                .toArray()
                ))
        );
    }
}
