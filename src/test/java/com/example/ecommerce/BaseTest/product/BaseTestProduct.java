package com.example.ecommerce.BaseTest.product;

import com.example.ecommerce.TestDataUtil;
import com.example.ecommerce.dtos.image.CreateImageRequest;
import com.example.ecommerce.dtos.image.ImageResponse;
import com.example.ecommerce.dtos.product.CreateProductRequest;
import com.example.ecommerce.dtos.product.ProductResponse;
import com.example.ecommerce.dtos.product.ProductSummaryResponse;
import com.example.ecommerce.dtos.product.UpdateProductRequest;
import com.example.ecommerce.entitites.product.Product;
import com.example.ecommerce.entitites.product.productEntities.Brand;
import com.example.ecommerce.entitites.product.productEntities.Category;
import com.example.ecommerce.entitites.product.productEntities.Image;
import com.example.ecommerce.entitites.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.web.multipart.MultipartFile;

public abstract class BaseTestProduct {
    protected User user;
    protected Product product;
    protected Category category;
    protected Brand brand;
    protected Image image;
    protected MultipartFile imageFile;

    protected CreateProductRequest createProductRequest;
    protected UpdateProductRequest updateProductRequest;
    protected ProductResponse productResponse;
    protected ProductSummaryResponse productSummaryResponse;
    protected CreateImageRequest createImageRequest;
    protected ImageResponse imageResponse;
    @BeforeEach
    public void setupBase() {
        user = TestDataUtil.user();
        category = TestDataUtil.category();
        brand = TestDataUtil.brand();
        product = TestDataUtil.product();
        image = TestDataUtil.image();
        imageFile = TestDataUtil.imageFile();

        createProductRequest = TestDataUtil.createProductRequest();
        updateProductRequest = TestDataUtil.updateProductRequest();
        productResponse = TestDataUtil.productResponse();
        productSummaryResponse = TestDataUtil.productSummaryResponse();
        createImageRequest = TestDataUtil.createImageRequest();
        imageResponse = TestDataUtil.imageResponse();
    }
}
