package com.example.ecommerce.dtos.product;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.NullSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductPriceValidationTests {
    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidator() {
        validatorFactory.close();
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"0", "-0.01", "-1", "0.001", "10.999", "100000000", "99999999.999"})
    void shouldRejectInvalidPrice(String value) {
        BigDecimal price = value == null ? null : new BigDecimal(value);
        CreateProductRequest createRequest = CreateProductRequest.builder().price(price).build();
        UpdateProductRequest updateRequest = UpdateProductRequest.builder().price(price).build();

        assertAll(
                () -> assertFalse(validator.validateProperty(createRequest, "price").isEmpty(),
                        "Create request should reject price " + value),
                () -> assertFalse(validator.validateProperty(updateRequest, "price").isEmpty(),
                        "Update request should reject price " + value)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"0.01", "10", "12.34", "99999999.99"})
    void shouldAcceptValidPrice(String value) {
        BigDecimal price = value == null ? null : new BigDecimal(value);
        CreateProductRequest createRequest = CreateProductRequest.builder().price(price).build();
        UpdateProductRequest updateRequest = UpdateProductRequest.builder().price(price).build();

        assertAll(
                () -> assertTrue(validator.validateProperty(createRequest, "price").isEmpty(),
                        "Create request should accept price " + value),
                () -> assertTrue(validator.validateProperty(updateRequest, "price").isEmpty(),
                        "Update request should accept price " + value)
        );
    }
}
