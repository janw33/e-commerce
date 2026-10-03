package com.example.ecommerce.services.category;

import com.example.ecommerce.BaseTest.category.BaseTestCategory;
import com.example.ecommerce.dtos.category.CategorySummaryResponse;
import com.example.ecommerce.dtos.category.SummaryCategoryWithChildrenProjection;
import com.example.ecommerce.exceptions.user.UserNotFoundException;
import com.example.ecommerce.mappers.category.CategoryMapper;
import com.example.ecommerce.providers.user.CurrentUserProvider;
import com.example.ecommerce.repositories.category.CategoryRepository;
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
                () -> categoryService.getCategories(authentication, null, Pageable.unpaged())
        );

        verify(categoryRepository, never()).findCategories(any(), any());
    }

    @Test
    void shouldReturnCategories() {
        when(currentUserProvider.get(authentication))
                .thenReturn(user);

        SummaryCategoryWithChildrenProjection projection =
                mock(SummaryCategoryWithChildrenProjection.class);

        when(projection.getId()).thenReturn(category.getId());
        when(projection.getName()).thenReturn(category.getName());
        when(projection.getSizeType()).thenReturn(category.getSizeType());
        when(projection.getHasChildren()).thenReturn(true);

        Page<SummaryCategoryWithChildrenProjection> page =
                new PageImpl<>(List.of(projection));

        when(categoryRepository.findCategories(null, Pageable.unpaged()))
                .thenReturn(page);

        Page<CategorySummaryResponse> categories =
                categoryService.getCategories(
                        authentication,
                        null,
                        Pageable.unpaged()
                );

        assertEquals(1, categories.getTotalElements());

        CategorySummaryResponse response = categories.getContent().getFirst();

        assertEquals(projection.getId(), response.getId());
        assertEquals(projection.getName(), response.getName());
        assertEquals(projection.getSizeType(), response.getSizeType());
        assertTrue(response.isHasChildren());

        verify(categoryRepository)
                .findCategories(null, Pageable.unpaged());
    }
}
