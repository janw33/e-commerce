package com.example.ecommerce.controllers.product;

import com.example.ecommerce.BaseTest.product.BaseTestProduct;
import com.example.ecommerce.TestDataUtil;
import com.example.ecommerce.dtos.product.UpdateProductRequest;
import com.example.ecommerce.entitites.product.enums.Color;
import com.example.ecommerce.exceptions.brand.BrandNotFoundException;
import com.example.ecommerce.exceptions.category.CategoryNotFoundException;
import com.example.ecommerce.exceptions.image.InvalidImageCountException;
import com.example.ecommerce.exceptions.product.InvalidSizeTypeException;
import com.example.ecommerce.exceptions.product.ProductNotFoundException;
import com.example.ecommerce.exceptions.user.UserNotFoundException;
import com.example.ecommerce.services.product.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockPart;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UpdateProductTests extends BaseTestProduct {
    private static final String PRODUCT_PATH = "/api/v1/products/{id}";

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

    private MockMultipartHttpServletRequestBuilder multipartProduct(UpdateProductRequest request) {
        MockPart productPart = new MockPart("product", objectMapper.writeValueAsBytes(request));
        productPart.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return MockMvcRequestBuilders.multipart(HttpMethod.PUT, PRODUCT_PATH, product.getId()).part(productPart);
    }

    private void performPut(UpdateProductRequest request, ResultMatcher... matchers) throws Exception {
        mockMvc.perform(
                multipartProduct(request)
                        .file(TestDataUtil.imageFile())
                        .with(authenticatedUser())
        ).andExpectAll(matchers);
    }

    @Test
    void shouldReturnHttp401WhenUserIsUnauthenticated() throws Exception {
        mockMvc.perform(
                multipartProduct(updateProductRequest).file(TestDataUtil.imageFile()))
                .andExpect(
                    status().isUnauthorized()
                );

        verifyNoInteractions(productService);
    }

    @Test
    void shouldReturnHttp400WhenProductPartIsMissing() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.multipart(HttpMethod.PUT, PRODUCT_PATH, product.getId())
                        .file(TestDataUtil.imageFile())
                        .with(authenticatedUser())
        ).andExpect(
                status().isBadRequest())
                .andExpect(
                        result -> assertEquals("product",
                        assertInstanceOf(MissingServletRequestPartException.class,
                                result.getResolvedException()).getRequestPartName())
                );

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
        updateProductRequest.setPrice(BigDecimal.valueOf(0));
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
        when(productService.updateProduct(any(), any(), any(), eq(product.getId())))
                .thenThrow(new UserNotFoundException());

        performPut(
                updateProductRequest,
                status().isNotFound(),
                jsonPath("$.error").value("USER_NOT_FOUND"),
                jsonPath("$.message").value("User not found"));
    }

    @Test
    void shouldReturnHttp404WhenProductIsNotFound() throws Exception {
        when(productService.updateProduct(any(), any(), any(), eq(product.getId())))
                .thenThrow(new ProductNotFoundException());

        performPut(
                updateProductRequest,
                status().isNotFound(),
                jsonPath("$.error").value("PRODUCT_NOT_FOUND"),
                jsonPath("$.message").value("Product not found"));
    }

    @Test
    void shouldReturnHttp400WhenCategoryAndProductSizeTypesDontMatch() throws Exception {
        when(productService.updateProduct(any(), any(), any(), eq(product.getId())))
                .thenThrow(new InvalidSizeTypeException());

        performPut(
                updateProductRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("INVALID_SIZE_TYPE"),
                jsonPath("$.message").value("Invalid size type for the selected category"));
    }

    @Test
    void shouldReturnHttp404WhenCategoryIsNotFound() throws Exception {
        when(productService.updateProduct(any(), any(), any(), eq(product.getId())))
                .thenThrow(new CategoryNotFoundException());

        performPut(
                updateProductRequest,
                status().isNotFound(),
                jsonPath("$.error").value("CATEGORY_NOT_FOUND"),
                jsonPath("$.message").value("Category not found"));
    }

    @Test
    void shouldReturnHttp404WhenBrandIsNotFound() throws Exception {
        when(productService.updateProduct(any(), any(), any(), eq(product.getId())))
                .thenThrow(new BrandNotFoundException());

        performPut(
                updateProductRequest,
                status().isNotFound(),
                jsonPath("$.error").value("BRAND_NOT_FOUND"),
                jsonPath("$.message").value("Brand not found"));
    }

    @Test
    void shouldReturnHttp400WhenImageCountExceedsLimit() throws Exception {
        var request = multipartProduct(updateProductRequest);

        for (int i = 0; i < 21; i++) {
            request.file(TestDataUtil.imageFile());
        }
        when(productService.updateProduct(any(), argThat(images -> images.size() == 21), any(), eq(product.getId())))
                .thenThrow(new InvalidImageCountException());

        mockMvc.perform(request.with(authenticatedUser()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("INVALID_IMAGE_COUNT"))
                .andExpect(jsonPath("$.message").value("Product must have between 1 and 20 images"));
    }

    @Test
    void shouldReturnHttp200WhenRequestIsValid() throws Exception {
        String imageUrl = "/api/v1/image/files/stored-image.png";
        productResponse.getImages().getFirst().setUrl(imageUrl);
        when(productService.updateProduct(any(), any(), any(), eq(product.getId())))
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
                jsonPath("$.images[0].url").value(imageUrl),

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
                )));

        ArgumentCaptor<UpdateProductRequest> productPart = ArgumentCaptor.forClass(UpdateProductRequest.class);
        ArgumentCaptor<List<MultipartFile>> imageParts = ArgumentCaptor.captor();
        verify(productService).updateProduct(productPart.capture(), imageParts.capture(),
                any(Authentication.class), eq(product.getId()));

        UpdateProductRequest capturedRequest = productPart.getValue();
        assertEquals(updateProductRequest.getTitle(), capturedRequest.getTitle());
        assertEquals(updateProductRequest.getDescription(), capturedRequest.getDescription());
        assertEquals(updateProductRequest.getCategoryId(), capturedRequest.getCategoryId());
        assertEquals(updateProductRequest.getBrandId(), capturedRequest.getBrandId());
        assertEquals(updateProductRequest.getSize(), capturedRequest.getSize());
        assertEquals(updateProductRequest.getCondition(), capturedRequest.getCondition());
        assertEquals(updateProductRequest.getColors(), capturedRequest.getColors());
        assertEquals(updateProductRequest.getMaterials(), capturedRequest.getMaterials());
        assertEquals(updateProductRequest.getPrice(), capturedRequest.getPrice());
        assertEquals(updateProductRequest.getShippingSize(), capturedRequest.getShippingSize());
        assertEquals(1, imageParts.getValue().size());
        MultipartFile uploadedImage = imageParts.getValue().getFirst();
        assertEquals(imageFile.getOriginalFilename(), uploadedImage.getOriginalFilename());
        assertArrayEquals(imageFile.getBytes(), uploadedImage.getBytes());
    }

    @Test
    void shouldReturnHttp200WhenImagesPartIsMissing() throws Exception {
        when(productService.updateProduct(any(), isNull(), any(), eq(product.getId())))
                .thenReturn(productResponse);

        mockMvc.perform(
                multipartProduct(updateProductRequest).with(authenticatedUser())
        ).andExpectAll(
                status().isOk(),
                jsonPath("$.id").value(product.getId()),
                jsonPath("$.images[0].url").value(productResponse.getImages().getFirst().getUrl())
        );

        verify(productService).updateProduct(any(UpdateProductRequest.class), isNull(),
                any(Authentication.class), eq(product.getId()));
    }
}
