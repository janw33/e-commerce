package com.example.ecommerce.BaseTest.category;

import com.example.ecommerce.TestDataUtil;
import com.example.ecommerce.dtos.category.CategoryDetailsResponse;
import com.example.ecommerce.dtos.category.CategorySummaryResponse;
import com.example.ecommerce.entitites.product.productEntities.Category;
import com.example.ecommerce.entitites.user.User;
import org.junit.jupiter.api.BeforeEach;

public abstract class BaseTestCategory {
    protected User user;
    protected Category category;

    protected CategorySummaryResponse categorySummaryResponse;
    protected CategoryDetailsResponse categoryDetailsResponse;

    @BeforeEach
    public void setupBase() {
        user = TestDataUtil.user();
        category = TestDataUtil.category();

        categorySummaryResponse = TestDataUtil.categorySummaryResponse();
        categoryDetailsResponse = TestDataUtil.categoryDetailsResponse();
    }
}
