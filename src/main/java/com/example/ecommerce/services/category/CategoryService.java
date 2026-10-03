package com.example.ecommerce.services.category;

import com.example.ecommerce.dtos.category.CategoryResponse;
import com.example.ecommerce.entitites.product.productEntities.Category;
import com.example.ecommerce.entitites.user.User;
import com.example.ecommerce.exceptions.UserNotFoundException;
import com.example.ecommerce.mappers.category.CategoryMapper;
import com.example.ecommerce.providers.user.CurrentUserProvider;
import com.example.ecommerce.repositories.category.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {
    private final CurrentUserProvider currentUserProvider;
    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;

    public CategoryService(CurrentUserProvider currentUserProvider, CategoryMapper categoryMapper, CategoryRepository categoryRepository) {
        this.currentUserProvider = currentUserProvider;
        this.categoryMapper = categoryMapper;
        this.categoryRepository = categoryRepository;
    }

    public Page<CategoryResponse> getCategories(
            Authentication authentication,
            Long parentId,
            Pageable pageable
    ) {
        currentUserProvider.get(authentication);

        return categoryRepository
                .findCategories(parentId, pageable)
                .map(categoryMapper::toResponse);
    }
}
