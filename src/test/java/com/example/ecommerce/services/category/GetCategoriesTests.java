package com.example.ecommerce.services.category;

import com.example.ecommerce.BaseTest.category.BaseTestCategory;
import com.example.ecommerce.dtos.category.CategorySummaryResponse;
import com.example.ecommerce.dtos.category.CategorySummaryProjection;
import com.example.ecommerce.exceptions.user.UserNotFoundException;
import com.example.ecommerce.mappers.category.CategoryMapper;
import com.example.ecommerce.providers.CurrentUserProvider;
import com.example.ecommerce.repository.category.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GetCategoriesTests extends BaseTestCategory {
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
                () -> categoryService.getCategories(authentication, Pageable.unpaged())
        );

        verify(categoryRepository, never()).getCategories(any());
    }

    @Test
    void shouldReturnCategories() {
        when(currentUserProvider.get(authentication))
                .thenReturn(user);

        CategorySummaryProjection projection =
                mock(CategorySummaryProjection.class);

        when(projection.getId()).thenReturn(category.getId());
        when(projection.getName()).thenReturn(category.getName());
        when(projection.getParentId()).thenReturn(category.getParent().getId());
        when(projection.getHasChildren()).thenReturn(true);

        Page<CategorySummaryProjection> page =
                new PageImpl<>(List.of(projection));

        when(categoryRepository.getCategories( Pageable.unpaged()))
                .thenReturn(page);

        Page<CategorySummaryResponse> result = categoryService.getCategories(
                authentication, Pageable.unpaged());

        assertEquals(1, result.getTotalElements());

        CategorySummaryResponse first = result.getContent().getFirst();

        assertEquals(projection.getId(), first.getId());
        assertEquals(projection.getName(), first.getName());
        assertEquals(projection.getParentId(), first.getParentId());
        assertTrue(first.isHasChildren());

        verify(categoryRepository).getCategories(Pageable.unpaged());
    }
}
