package com.example.ecommerce.BaseTest.brand;

import com.example.ecommerce.TestDataUtil;
import com.example.ecommerce.dtos.brand.BrandResponse;
import com.example.ecommerce.entitites.product.productEntities.Brand;
import com.example.ecommerce.entitites.product.productEntities.Category;
import com.example.ecommerce.entitites.user.User;
import org.junit.jupiter.api.BeforeEach;

public abstract class BaseTestBrand {
    protected User user;
    protected Category category;
    protected Brand brand;

   protected BrandResponse brandResponse;
    @BeforeEach
    public void setupBase() {
        user = TestDataUtil.user();
        category = TestDataUtil.category();
        brand = TestDataUtil.brand();

        brandResponse = TestDataUtil.brandResponse();
    }
}
