package com.example.ecommerce.services.category;

import com.example.ecommerce.BaseTest.category.BaseTestCategory;
import com.example.ecommerce.dtos.category.CategoryDetailsResponse;
import com.example.ecommerce.dtos.category.DetailsCategoryWithChildrenProjection;
import com.example.ecommerce.exceptions.category.CategoryNotFoundException;
import com.example.ecommerce.exceptions.user.UserNotFoundException;
import com.example.ecommerce.mappers.category.CategoryMapper;
import com.example.ecommerce.providers.CurrentUserProvider;
import com.example.ecommerce.repositories.category.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GetCategoryTests extends BaseTestCategory {
    private final CategoryMapper categoryMapper = new CategoryMapper();

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
                categoryMapper,
                categoryRepository
        );
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenUserDoesntExists() {
        when(currentUserProvider.get(authentication))
                .thenThrow(new UserNotFoundException());

        assertThrows(
                UserNotFoundException.class,
                () -> categoryService.getCategory(authentication, category.getId())
        );

        verify(categoryRepository, never()).findCategory(category.getId());
    }

    @Test
    void shouldThrowCategoryNotFoundWhenCategoryDoesntExists() {
        when(currentUserProvider.get(authentication))
                .thenReturn(user);

        when(categoryRepository.findCategory(category.getId()))
                .thenReturn(Optional.empty());

        assertThrows(
                CategoryNotFoundException.class,
                () -> categoryService.getCategory(authentication, category.getId())
        );

        verify(categoryRepository).findCategory(category.getId());
    }

    @Test
    void shouldReturnCategory() {
        when(currentUserProvider.get(authentication))
                .thenReturn(user);

        DetailsCategoryWithChildrenProjection projection =
                mock(DetailsCategoryWithChildrenProjection.class);

        when(projection.getId()).thenReturn(category.getId());
        when(projection.getName()).thenReturn(category.getName());
        when(projection.getSizeType()).thenReturn(category.getSizeType());
        when(projection.getParentId()).thenReturn(category.getParent().getId());
        when(projection.getHasChildren()).thenReturn(true);

        when(categoryRepository.findCategory(category.getId()))
                .thenReturn(Optional.of(projection));

        CategoryDetailsResponse result = categoryService.getCategory(authentication, category.getId());

        assertEquals(projection.getId(), result.getId());
        assertEquals(projection.getName(), result.getName());
        assertEquals(projection.getSizeType(), result.getSizeType());
        assertEquals(projection.getParentId(), result.getParentId());
        assertTrue(result.isHasChildren());

        verify(categoryRepository).findCategory(category.getId());
    }
}
