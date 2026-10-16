package com.example.ecommerce.services.category;

import com.example.ecommerce.BaseTest.category.BaseTestCategory;
import com.example.ecommerce.entitites.product.enums.Size;
import com.example.ecommerce.entitites.product.enums.SizeType;
import com.example.ecommerce.exceptions.category.CategoryNotFoundException;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GetSizesTests extends BaseTestCategory {
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
                () -> categoryService.getSizes(category.getId(), authentication)
        );

        verifyNoInteractions(categoryRepository);
    }

    @Test
    void shouldReturnAllSizesWhenCategoryIdIsNull() {
        when(currentUserProvider.get(authentication))
                .thenReturn(user);

        List<Size> result = categoryService.getSizes(null, authentication);

        assertEquals(Arrays.asList(Size.values()), result);

        verifyNoInteractions(categoryRepository);
    }

    @Test
    void shouldThrowCategoryNotFoundExceptionWhenCategoryDoesntExist() {
        when(currentUserProvider.get(authentication))
                .thenReturn(user);

        when(categoryRepository.findById(category.getId()))
                .thenReturn(Optional.empty());

        assertThrows(
                CategoryNotFoundException.class,
                () -> categoryService.getSizes(category.getId(), authentication)
        );

        verify(categoryRepository).findById(category.getId());
    }

    @Test
    void shouldReturnSizesMatchingCategorySizeType() {
        category.setSizeType(SizeType.CLOTHING);

        when(currentUserProvider.get(authentication))
                .thenReturn(user);

        when(categoryRepository.findById(category.getId()))
                .thenReturn(Optional.of(category));

        List<Size> result = categoryService.getSizes(category.getId(), authentication);

        List<Size> expected = Arrays.stream(Size.values())
                .filter(size -> size.getType() == SizeType.CLOTHING)
                .toList();

        assertEquals(expected, result);

        verify(categoryRepository).findById(category.getId());
    }
}
