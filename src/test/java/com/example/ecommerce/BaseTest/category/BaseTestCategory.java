package com.example.ecommerce.BaseTest.category;

import com.example.ecommerce.TestDataUtil;
import com.example.ecommerce.dtos.category.CategoryResponse;
import com.example.ecommerce.dtos.category.CategoryWithChildrenProjection;
import com.example.ecommerce.entitites.product.productEntities.Category;
import com.example.ecommerce.entitites.user.User;
import org.junit.jupiter.api.BeforeEach;

public abstract class BaseTestCategory {
    protected User user;
    protected Category category;

    protected CategoryResponse categoryResponse;
    @BeforeEach
    public void setupBase() {
        user = TestDataUtil.user();
        category = TestDataUtil.category();

        categoryResponse = TestDataUtil.categoryResponse();
    }
}
