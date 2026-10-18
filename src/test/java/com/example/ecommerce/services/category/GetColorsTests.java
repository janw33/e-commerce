package com.example.ecommerce.services.category;

import com.example.ecommerce.BaseTest.category.BaseTestCategory;
import com.example.ecommerce.entitites.product.enums.Color;
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

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GetColorsTests extends BaseTestCategory {
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
    void shouldThrowUserNotFoundExceptionWhenUserDoesntExists() {
        when(currentUserProvider.get(authentication))
                .thenThrow(new UserNotFoundException());

        assertThrows(
                UserNotFoundException.class,
                () -> categoryService.getColors(authentication)
        );

        verify(currentUserProvider).get(authentication);
    }

    @Test
    void shouldReturnAllColorsWhenUserExists() {
        when(currentUserProvider.get(authentication))
                .thenReturn(user);

        List<Color> result = categoryService.getColors(authentication);

        assertEquals(Arrays.asList(Color.values()), result);

        verify(currentUserProvider).get(authentication);
    }
}
