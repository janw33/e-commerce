package com.example.ecommerce.services.category;

import com.example.ecommerce.BaseTest.category.BaseTestCategory;
import com.example.ecommerce.entitites.product.enums.Condition;
import com.example.ecommerce.exceptions.user.UserNotFoundException;
import com.example.ecommerce.mappers.category.CategoryMapper;
import com.example.ecommerce.providers.CurrentUserProvider;
import com.example.ecommerce.repository.category.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetConditionsTests extends BaseTestCategory {
    @Mock
    private CurrentUserProvider currentUserProvider;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private Authentication authentication;

    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        categoryService = new CategoryService(
                currentUserProvider,
                new CategoryMapper(),
                categoryRepository
        );
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenUserDoesntExist() {
        when(currentUserProvider.get(authentication))
            .thenThrow(UserNotFoundException.class);

        assertThrows(
                UserNotFoundException.class,
                () -> categoryService.getConditions(authentication)
        );

        verify(currentUserProvider).get(authentication);
    }

    @Test
    void shouldReturnAllConditionsWhenUserExists() {
        when(currentUserProvider.get(authentication))
                .thenReturn(user);

        List<Condition> result = categoryService.getConditions(authentication);

        assertEquals(List.of(Condition.values()), result);

        verify(currentUserProvider).get(authentication);
    }
}
