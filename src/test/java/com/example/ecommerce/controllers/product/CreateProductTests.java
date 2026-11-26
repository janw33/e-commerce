package com.example.ecommerce.controllers.product;

import com.example.ecommerce.BaseTest.product.BaseTestProduct;
import com.example.ecommerce.TestDataUtil;
import com.example.ecommerce.dtos.product.CreateProductRequest;
import com.example.ecommerce.entitites.product.enums.Color;
import com.example.ecommerce.exceptions.brand.BrandNotFoundException;
import com.example.ecommerce.exceptions.category.CategoryNotFoundException;
import com.example.ecommerce.exceptions.image.InvalidImageCountException;
import com.example.ecommerce.exceptions.user.UserNotFoundException;
import com.example.ecommerce.services.product.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CreateProductTests extends BaseTestProduct {
    private static final String PRODUCTS_PATH = "/api/v1/products";

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

    private MockMultipartHttpServletRequestBuilder multipartProduct(CreateProductRequest request) {
        MockPart productPart = new MockPart("product", objectMapper.writeValueAsBytes(request));
        productPart.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return MockMvcRequestBuilders.multipart(PRODUCTS_PATH).part(productPart);
    }

    private void performPost(CreateProductRequest request, ResultMatcher... matchers) throws Exception {
        mockMvc.perform(
                multipartProduct(request)
                        .file(TestDataUtil.imageFile())
                        .with(authenticatedUser())
        ).andExpectAll(matchers);
    }

    @Test
    void shouldReturnHttp401WhenUserIsUnauthenticated() throws Exception {
        mockMvc.perform(
                multipartProduct(createProductRequest).file(TestDataUtil.imageFile())
        ).andExpect(status().isUnauthorized());

        verifyNoInteractions(productService);
    }

    @Test
    void shouldReturnHttp400WhenProductPartIsMissing() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.multipart(PRODUCTS_PATH)
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
    void shouldReturnHttp400WhenImagesPartIsMissing() throws Exception {
        mockMvc.perform(
                multipartProduct(createProductRequest).with(authenticatedUser())
        ).andExpect(
                status().isBadRequest())
                .andExpect(result -> assertEquals("images",
                        assertInstanceOf(MissingServletRequestPartException.class,
                                result.getResolvedException()).getRequestPartName())
                );

        verifyNoInteractions(productService);
    }

    @Test
    void shouldReturnHttp400WhenTitleIsNull() throws Exception {
        createProductRequest.setTitle(null);
        performPost(
                createProductRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.title").value("Title cannot be blank"));
    }

    @Test
    void shouldReturnHttp400WhenTitleIsBlank() throws Exception {
        createProductRequest.setTitle("     ");
        performPost(
                createProductRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.title").value("Title cannot be blank"));
    }

    @Test
    void shouldReturnHttp400WhenTitleIsTooShort() throws Exception {
        createProductRequest.setTitle("a");
        performPost(
                createProductRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.title").value("Title must be between 5 and 100 characters"));
    }

    @Test
    void shouldReturnHttp400WhenTitleIsTooLong() throws Exception {
        createProductRequest.setTitle("a".repeat(101));
        performPost(
                createProductRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.title").value("Title must be between 5 and 100 characters"));
    }

    @Test
    void shouldReturnHttp400WhenDescriptionIsNull() throws Exception {
        createProductRequest.setDescription(null);
        performPost(
                createProductRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.description").value("Description cannot be blank"));
    }

    @Test
    void shouldReturnHttp400WhenDescriptionIsBlank() throws Exception {
        createProductRequest.setDescription("     ");
        performPost(
                createProductRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.description").value("Description cannot be blank"));
    }

    @Test
    void shouldReturnHttp400WhenDescriptionIsTooShort() throws Exception {
        createProductRequest.setDescription("a");
        performPost(
                createProductRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.description").value("Description must be between 5 and 750 characters"));
    }

    @Test
    void shouldReturnHttp400WhenDescriptionIsTooLong() throws Exception {
        createProductRequest.setDescription("a".repeat(751));
        performPost(
                createProductRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.description").value("Description must be between 5 and 750 characters"));
    }

    @Test
    void shouldReturnHttp400WhenCategoryIdIsNull() throws Exception {
        createProductRequest.setCategoryId(null);
        performPost(
                createProductRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.categoryId").value("Category id cannot be null"));
    }

    @Test
    void shouldReturnHttp400WhenBrandIdIsNull() throws Exception {
        createProductRequest.setBrandId(null);
        performPost(
                createProductRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.brandId").value("Brand id cannot be null"));
    }

    @Test
    void shouldReturnHttp400WhenSizeIsNull() throws Exception {
        createProductRequest.setSize(null);
        performPost(
                createProductRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.size").value("Size cannot be null"));
    }

    @Test
    void shouldReturnHttp400WhenConditionIsNull() throws Exception {
        createProductRequest.setCondition(null);
        performPost(
                createProductRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.condition").value("Condition cannot be null"));
    }

    @Test
    void shouldReturnHttp400WhenColorsAreNull() throws Exception {
        createProductRequest.setColors(null);
        performPost(
                createProductRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.colors").value("Colors cannot be empty"));
    }

    @Test
    void shouldReturnHttp400WhenColorsAreEmpty() throws Exception {
        createProductRequest.setColors(Set.of());
        performPost(
                createProductRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.colors").value("Colors cannot be empty"));
    }

    @Test
    void shouldReturnHttp400WhenNumberOfColorsIsGreaterThanTwo() throws Exception {
        createProductRequest.setColors(Set.of(Color.BLUE, Color.BLACK, Color.BEIGE));
        performPost(
                createProductRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.colors").value("Number of colors must not exceed 2"));
    }

    @Test
    void shouldReturnHttp400WhenPriceIsNull() throws Exception {
        createProductRequest.setPrice(null);
        performPost(
                createProductRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.price").value("Price cannot be null"));
    }

    @Test
    void shouldReturnHttp400WhenPriceIsNegative() throws Exception {
        createProductRequest.setPrice(BigDecimal.valueOf(-1));
        performPost(
                createProductRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.price").value("Price must be greater than 0"));
    }

    @Test
    void shouldReturnHttp400WhenPriceIsZero() throws Exception {
        createProductRequest.setPrice(BigDecimal.valueOf(0));
        performPost(
                createProductRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.price").value("Price must be greater than 0"));
    }

    @Test
    void shouldReturnHttp400WhenPriceIsBadFormat() throws Exception {
        createProductRequest.setPrice(BigDecimal.valueOf(0.001));

        performPost(
                createProductRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.price").value("Price must have at most 8 integer digits and 2 decimal places"));

        verifyNoInteractions(productService);
    }

    @Test
    void shouldReturnHttp400WhenPriceIsTooBig() throws Exception {
        createProductRequest.setPrice(BigDecimal.valueOf(999999999));

        performPost(
                createProductRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.price").value("Price must have at most 8 integer digits and 2 decimal places"));

        verifyNoInteractions(productService);
    }

    @Test
    void shouldReturnHttp400WhenShippingSizeIsNull() throws Exception {
        createProductRequest.setShippingSize(null);
        performPost(
                createProductRequest,
                status().isBadRequest(),
                jsonPath("$.error").value("VALIDATION_ERROR"),
                jsonPath("$.validationErrors.shippingSize").value("Shipping size cannot be null"));
    }

    @Test
    void shouldReturnHttp404WhenUserIsNotFound() throws Exception {
        when(productService.createProduct(any(), any(), any()))
                .thenThrow(new UserNotFoundException());

        performPost(
                createProductRequest,
                status().isNotFound(),
                jsonPath("$.error").value("USER_NOT_FOUND"),
                jsonPath("$.message").value("User not found"));
    }

    @Test
    void shouldReturnHttp404WhenCategoryIsNotFound() throws Exception {
        when(productService.createProduct(any(), any(), any()))
                .thenThrow(new CategoryNotFoundException());

        performPost(
                createProductRequest,
                status().isNotFound(),
                jsonPath("$.error").value("CATEGORY_NOT_FOUND"),
                jsonPath("$.message").value("Category not found"));
    }

    @Test
    void shouldReturnHttp404WhenBrandIsNotFound() throws Exception {
        when(productService.createProduct(any(), any(), any()))
                .thenThrow(new BrandNotFoundException());

        performPost(
                createProductRequest,
                status().isNotFound(),
                jsonPath("$.error").value("BRAND_NOT_FOUND"),
                jsonPath("$.message").value("Brand not found"));
    }

    @Test
    void shouldReturnHttp400WhenImageCountExceedsLimit() throws Exception {
        var request = multipartProduct(createProductRequest);

        for (int i = 0; i < 21; i++) {
            request.file(TestDataUtil.imageFile());
        }
        when(productService.createProduct(any(), argThat(images -> images.size() == 21), any()))
                .thenThrow(new InvalidImageCountException());

        mockMvc.perform(request.with(authenticatedUser()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("INVALID_IMAGE_COUNT"))
                .andExpect(jsonPath("$.message").value("Product must have between 1 and 20 images"));
    }

    @Test
    void shouldReturnHttp201WhenRequestIsValid() throws Exception {
        String imageUrl = "/api/v1/image/files/stored-image.png";
        productResponse.getImages().getFirst().setUrl(imageUrl);
        when(productService.createProduct(any(), any(), any()))
                .thenReturn(productResponse);

        performPost(
                createProductRequest,
                status().isCreated(),
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
                jsonPath("$.likeCount").value(0),
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

        ArgumentCaptor<CreateProductRequest> productPart = ArgumentCaptor.forClass(CreateProductRequest.class);
        ArgumentCaptor<List<MultipartFile>> imageParts = ArgumentCaptor.captor();
        verify(productService).createProduct(productPart.capture(), imageParts.capture(), any(Authentication.class));

        assertEquals(createProductRequest.getTitle(), productPart.getValue().getTitle());
        assertEquals(1, imageParts.getValue().size());
        MultipartFile uploadedImage = imageParts.getValue().getFirst();
        assertEquals(imageFile.getOriginalFilename(), uploadedImage.getOriginalFilename());
        assertArrayEquals(imageFile.getBytes(), uploadedImage.getBytes());
    }
}
