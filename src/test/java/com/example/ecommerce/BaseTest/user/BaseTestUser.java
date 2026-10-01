package com.example.ecommerce.BaseTest.user;

import com.example.ecommerce.TestDataUtil;
import com.example.ecommerce.dtos.auth.UserResponse;
import com.example.ecommerce.entitites.user.User;
import org.junit.jupiter.api.BeforeEach;

public abstract class BaseTestUser {
    protected User user;
    protected UserResponse userResponse;

    @BeforeEach
    public void setUp() {
        user = TestDataUtil.user();
        userResponse = TestDataUtil.userResponse();
    }
}
