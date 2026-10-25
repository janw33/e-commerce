package com.example.ecommerce.controllers.product;

import com.example.ecommerce.BaseTest.product.BaseTestProduct;
import com.example.ecommerce.dtos.product.ProductSummaryResponse;
import com.example.ecommerce.entitites.product.enums.ProductSortBy;
import com.example.ecommerce.exceptions.user.UserNotFoundException;
import com.example.ecommerce.services.product.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
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
public class GetSellerProductsTests extends BaseTestProduct {
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
                MockMvcRequestBuilders.get("/api/v1/products/seller").param("sellerId", "2")
                        .with(authenticatedUser())
        );

        for (ResultMatcher matcher : matchers) {
            result.andExpect(matcher);
        }
    }

    @Test
    void shouldReturnHttp401WhenUserIsUnauthenticated() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/products/seller").param("sellerId", "2"))
                .andExpect(
                        status().isUnauthorized()
                );
    }

    @Test
    void shouldReturnHttp400WhenSortByIsInvalid() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/products/seller")
                                .param("sellerId", "2")
                                .param("sortBy", "invalid")
                                .with(authenticatedUser()))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(productService);
    }

    @Test
    void shouldReturnHttp400WhenSellerIdIsMissing() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/products/seller")
                                .with(authenticatedUser()))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(productService);
    }

    @Test
    void shouldReturnHttp400WhenSellerIdIsInvalid() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/products/seller")
                                .param("sellerId", "invalid")
                                .with(authenticatedUser()))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(productService);
    }

    @Test
    void shouldReturnHttp404WhenUserIsNotFound() throws Exception {
        when(productService.getSellerProducts(any(), eq(2L), any(), any()))
                .thenThrow(new UserNotFoundException());

        performGet(
                status().isNotFound(),
                jsonPath("$.error").value("USER_NOT_FOUND"),
                jsonPath("$.message").value("User not found")
        );
    }

    @Test
    void shouldReturnHttp200WhenRequestIsValid() throws Exception {
        ProductSummaryResponse response = ProductSummaryResponse.builder()
                .id(product.getId())
                .imageUrl(image.getUrl())
                .likeCount(5L)
                .title(product.getTitle())
                .price(product.getPrice())
                .condition(product.getCondition())
                .size(product.getSize())
                .brandName(brand.getName())
                .build();

        Page<ProductSummaryResponse> page = new PageImpl<>(List.of(response));

        when(productService.getSellerProducts(any(), eq(2L), any(), any()))
                .thenReturn(page);

        performGet(
                status().isOk(),
                jsonPath("$.content").isArray(),
                jsonPath("$.totalElements").value(1),
                jsonPath("$.content[0].id").value(product.getId()),
                jsonPath("$.content[0].imageUrl").value(image.getUrl()),
                jsonPath("$.content[0].likeCount").value(5),
                jsonPath("$.content[0].title").value(product.getTitle()),
                jsonPath("$.content[0].price").value(product.getPrice()),
                jsonPath("$.content[0].condition").value(product.getCondition().name()),
                jsonPath("$.content[0].size").value(product.getSize().name()),
                jsonPath("$.content[0].brandName").value(brand.getName())
        );

        verify(productService).getSellerProducts(any(), eq(2L), eq(ProductSortBy.NEWEST), eq(PageRequest.of(0, 100)));
    }


    @Test
    void shouldReturnHttp200WhenSellerHasNoProducts() throws Exception {
        when(productService.getSellerProducts(any(), eq(2L), any(), any()))
                .thenReturn(Page.empty(PageRequest.of(0, 100)));

        performGet(
                status().isOk(),
                jsonPath("$.content").isEmpty(),
                jsonPath("$.totalElements").value(0)
        );
    }

    @Test
    void shouldPassSortByAndPaginationToService() throws Exception {
        Pageable pageable = PageRequest.of(1, 5);
        Page<ProductSummaryResponse> page = new PageImpl<>(List.of(productSummaryResponse), pageable, 12);

        when(productService.getSellerProducts(any(), eq(2L), eq(ProductSortBy.PRICE_ASC), eq(pageable)))
                .thenReturn(page);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/products/seller")
                                .param("sellerId", "2")
                                .param("sortBy", "PRICE_ASC")
                                .param("page", "1")
                                .param("size", "5")
                                .with(authenticatedUser()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(1))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.totalElements").value(12))
                .andExpect(jsonPath("$.totalPages").value(3));

        verify(productService).getSellerProducts(any(), eq(2L), eq(ProductSortBy.PRICE_ASC), eq(pageable));
    }
}
