package com.example.ecommerce.controllers.product;

import com.example.ecommerce.BaseTest.product.BaseTestProduct;
import com.example.ecommerce.dtos.image.CreateImageRequest;
import com.example.ecommerce.dtos.product.UpdateProductRequest;
import com.example.ecommerce.entitites.product.enums.Color;
import com.example.ecommerce.exceptions.brand.BrandNotFoundException;
import com.example.ecommerce.exceptions.category.CategoryNotFoundException;
import com.example.ecommerce.exceptions.product.ProductNotFoundException;
import com.example.ecommerce.exceptions.user.UserNotFoundException;
import com.example.ecommerce.services.product.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

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
public class UpdateProductTests extends BaseTestProduct {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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

    private void performPut(UpdateProductRequest request, ResultMatcher... matchers) throws Exception {
        String requestJson = objectMapper.writeValueAsString(request);

        var result = mockMvc.perform(
                MockMvcRequestBuilders.put("/api/v1/products/{productId}", product.getId())
                        .with(authenticatedUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
        );

        for (ResultMatcher matcher : matchers) {
            result.andExpect(matcher);
        }
    }

    @Test
    void shouldReturnHttp401WhenUserIsUnauthenticated() throws Exception {
        String requestJson = objectMapper.writeValueAsString(updateProductRequest);

        mockMvc.perform(
                MockMvcRequestBuilders.put("/api/v1/products/{productId}", product.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(
                        status().isUnauthorized()
                );

        verifyNoInteractions(productService);
    }

    @Test
    void shouldReturnHttp400WhenImageUrlIsNull() throws Exception {
        createImageRequest.setUrl(null);

        updateProductRequest.setImages(List.of(createImageRequest));

        performPut(
                updateProductRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors['images[0].url']").value("Image url cannot be empty"));
    }

    @Test
    void shouldReturnHttp400WhenImageUrlIsTooShort() throws Exception {
        createImageRequest.setUrl("a");

        updateProductRequest.setImages(List.of(createImageRequest));

        performPut(
                updateProductRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors['images[0].url']").value("Image url must be between 2 and 1000 characters"));
    }

    @Test
    void shouldReturnHttp400WhenImageUrlIsTooLong() throws Exception {
        createImageRequest.setUrl("a".repeat(1001));

        updateProductRequest.setImages(List.of(createImageRequest));

        performPut(
                updateProductRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors['images[0].url']").value("Image url must be between 2 and 1000 characters"));
    }

    @Test
    void shouldReturnHttp400WhenImagesAreNull() throws Exception {
        updateProductRequest.setImages(null);

        performPut(
                updateProductRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.images").value("Images cannot be empty"));

        verifyNoInteractions(productService);
    }

    @Test
    void shouldReturnHttp400WhenImagesAreEmpty() throws Exception {
        updateProductRequest.setImages(List.of());

        performPut(
                updateProductRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.images").value("Images cannot be empty"));

        verifyNoInteractions(productService);
    }

    @Test
    void shouldReturnHttp400WhenImagesSizeExceed20() throws Exception {
        updateProductRequest.setImages(
                IntStream.range(0, 21)
                        .mapToObj(i -> createImageRequest)
                        .toList()
        );

        performPut(
                updateProductRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.images").value("Number of images must not exceed 20"));

        verifyNoInteractions(productService);
    }

    @Test
    void shouldReturnHttp400WhenTitleIsNull() throws Exception {
        updateProductRequest.setTitle(null);

        performPut(
                updateProductRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.title").value("Title cannot be blank"));

        verifyNoInteractions(productService);
    }

    @Test
    void shouldReturnHttp400WhenTitleIsBlank() throws Exception {
        updateProductRequest.setTitle("     ");

        performPut(
                updateProductRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.title").value("Title cannot be blank"));

        verifyNoInteractions(productService);
    }

    @Test
    void shouldReturnHttp400WhenTitleIsTooShort() throws Exception {
        updateProductRequest.setTitle("a");

        performPut(
                updateProductRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.title").value("Title must be between 5 and 100 characters"));

        verifyNoInteractions(productService);
    }

    @Test
    void shouldReturnHttp400WhenTitleIsTooLong() throws Exception {
        updateProductRequest.setTitle("a".repeat(101));

        performPut(
                updateProductRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.title").value("Title must be between 5 and 100 characters"));

        verifyNoInteractions(productService);
    }

    @Test
    void shouldReturnHttp400WhenDescriptionIsNull() throws Exception {
        updateProductRequest.setDescription(null);

        performPut(
                updateProductRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.description").value("Description cannot be blank"));

        verifyNoInteractions(productService);
    }

    @Test
    void shouldReturnHttp400WhenDescriptionIsBlank() throws Exception {
        updateProductRequest.setDescription("     ");

        performPut(
                updateProductRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.description").value("Description cannot be blank"));

        verifyNoInteractions(productService);
    }

    @Test
    void shouldReturnHttp400WhenDescriptionIsTooShort() throws Exception {
        updateProductRequest.setDescription("a");

        performPut(
                updateProductRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.description").value("Description must be between 5 and 750 characters"));

        verifyNoInteractions(productService);
    }

    @Test
    void shouldReturnHttp400WhenDescriptionIsTooLong() throws Exception {
        updateProductRequest.setDescription("a".repeat(751));

        performPut(
                updateProductRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.description").value("Description must be between 5 and 750 characters"));

        verifyNoInteractions(productService);
    }

    @Test
    void shouldReturnHttp400WhenCategoryIdIsNull() throws Exception {
        updateProductRequest.setCategoryId(null);

        performPut(
                updateProductRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.categoryId").value("Category id cannot be null"));

        verifyNoInteractions(productService);
    }

    @Test
    void shouldReturnHttp400WhenBrandIdIsNull() throws Exception {
        updateProductRequest.setBrandId(null);

        performPut(
                updateProductRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.brandId").value("Brand id cannot be null"));

        verifyNoInteractions(productService);
    }

    @Test
    void shouldReturnHttp400WhenSizeIsNull() throws Exception {
        updateProductRequest.setSize(null);

        performPut(
                updateProductRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.size").value("Size cannot be null"));

        verifyNoInteractions(productService);
    }

    @Test
    void shouldReturnHttp400WhenConditionIsNull() throws Exception {
        updateProductRequest.setCondition(null);

        performPut(
                updateProductRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.condition").value("Condition cannot be null"));

        verifyNoInteractions(productService);
    }


    @Test
    void shouldReturnHttp400WhenColorsAreNull() throws Exception {
        updateProductRequest.setColors(null);

        performPut(
                updateProductRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.colors").value("Colors cannot be empty"));

        verifyNoInteractions(productService);
    }

    @Test
    void shouldReturnHttp400WhenColorsAreEmpty() throws Exception {
        updateProductRequest.setColors(Set.of());

        performPut(
                updateProductRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.colors").value("Colors cannot be empty"));

        verifyNoInteractions(productService);
    }

    @Test
    void shouldReturnHttp400WhenNumberOfColorsIsGreaterThanTwo() throws Exception {
        updateProductRequest.setColors(Set.of(Color.BLUE, Color.BLACK, Color.BEIGE));

        performPut(
                updateProductRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.colors").value("Number of colors must not exceed 2"));

        verifyNoInteractions(productService);
    }

    @Test
    void shouldReturnHttp400WhenPriceIsNull() throws Exception {
        updateProductRequest.setPrice(null);

        performPut(
                updateProductRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.price").value("Price cannot be null"));

        verifyNoInteractions(productService);
    }

    @Test
    void shouldReturnHttp400WhenPriceIsNegative() throws Exception {
        updateProductRequest.setPrice(BigDecimal.valueOf(-1));

        performPut(
                updateProductRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.price").value("Price must be greater than 0"));

        verifyNoInteractions(productService);
    }

    @Test
    void shouldReturnHttp400WhenPriceIsZero() throws Exception {
        updateProductRequest.setPrice(BigDecimal.ZERO);

        performPut(
                updateProductRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.price").value("Price must be greater than 0"));

        verifyNoInteractions(productService);
    }

    @Test
    void shouldReturnHttp400WhenPriceIsBadFormat() throws Exception {
        updateProductRequest.setPrice(BigDecimal.valueOf(0.001));

        performPut(
                updateProductRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.price").value("Price must have at most 8 integer digits and 2 decimal places"));

        verifyNoInteractions(productService);
    }

    @Test
    void shouldReturnHttp400WhenPriceIsTooBig() throws Exception {
        updateProductRequest.setPrice(BigDecimal.valueOf(999999999));

        performPut(
                updateProductRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.price").value("Price must have at most 8 integer digits and 2 decimal places"));

        verifyNoInteractions(productService);
    }

    @Test
    void shouldReturnHttp400WhenCurrencyIsNull() throws Exception {
        updateProductRequest.setCurrency(null);

        performPut(
                updateProductRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.currency").value("Currency cannot be null"));

        verifyNoInteractions(productService);
    }

    @Test
    void shouldReturnHttp400WhenShippingSizeIsNull() throws Exception {
        updateProductRequest.setShippingSize(null);

        performPut(
                updateProductRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.shippingSize").value("Shipping size cannot be null"));

        verifyNoInteractions(productService);
    }

    @Test
    void shouldReturnHttp404WhenUserIsNotFound() throws Exception {
        when(productService.updateProduct(any(), any(), eq(product.getId())))
                .thenThrow(new UserNotFoundException());

        performPut(
                updateProductRequest,
                status().isNotFound(),
                jsonPath("$.error").value("USER_NOT_FOUND"),
                jsonPath("$.message").value("User not found"));
    }

    @Test
    void shouldReturnHttp404WhenProductIsNotFound() throws Exception {
        when(productService.updateProduct(any(), any(), eq(product.getId())))
                .thenThrow(new ProductNotFoundException());

        performPut(
                updateProductRequest,
                status().isNotFound(),
                jsonPath("$.error").value("PRODUCT_NOT_FOUND"),
                jsonPath("$.message").value("Product not found"));
    }

    @Test
    void shouldReturnHttp404WhenCategoryIsNotFound() throws Exception {
        when(productService.updateProduct(any(), any(), eq(product.getId())))
                .thenThrow(new CategoryNotFoundException());

        performPut(
                updateProductRequest,
                status().isNotFound(),
                jsonPath("$.error").value("CATEGORY_NOT_FOUND"),
                jsonPath("$.message").value("Category not found"));
    }

    @Test
    void shouldReturnHttp404WhenBrandIsNotFound() throws Exception {
        when(productService.updateProduct(any(), any(), eq(product.getId())))
                .thenThrow(new BrandNotFoundException());

        performPut(
                updateProductRequest,
                status().isNotFound(),
                jsonPath("$.error").value("BRAND_NOT_FOUND"),
                jsonPath("$.message").value("Brand not found"));
    }

    @Test
    void shouldReturnHttp200WhenRequestIsValid() throws Exception {
        when(productService.updateProduct(any(), any(), eq(product.getId())))
                .thenReturn(productResponse);

        performPut(
                updateProductRequest,
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
                jsonPath("$.currency").value(product.getCurrency().toString()),
                jsonPath("$.condition").value(product.getCondition().toString()),
                jsonPath("$.size").value(product.getSize().toString()),
                jsonPath("$.shippingSize").value(product.getShippingSize().toString()),
                jsonPath("$.viewCount").value(product.getViewCount()),
                jsonPath("$.likeCount").value(productResponse.getLikeCount()),
                jsonPath("$.status").value(product.getStatus().toString()),
                jsonPath("$.createdAt").isNotEmpty(),
                jsonPath("$.updatedAt").isNotEmpty(),
                jsonPath("$.deletedAt").value(nullValue()),
                jsonPath("$.soldAt").value(nullValue()),
                jsonPath("$.images").isArray(),
                jsonPath("$.images.length()").value(productResponse.getImages().size()),
                jsonPath("$.images[0].url").value(productResponse.getImages().getFirst().getUrl()),
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
