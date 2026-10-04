package com.example.ecommerce.services.category;

import com.example.ecommerce.dtos.category.CategoryDetailsResponse;
import com.example.ecommerce.dtos.category.CategorySummaryResponse;
import com.example.ecommerce.exceptions.category.CategoryNotFoundException;
import com.example.ecommerce.mappers.category.CategoryMapper;
import com.example.ecommerce.providers.CurrentUserProvider;
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

    public Page<CategorySummaryResponse> getCategories(
            Authentication authentication,
            Long parentId,
            Pageable pageable
    ) {
        currentUserProvider.get(authentication);

        return categoryRepository
                .findCategories(parentId, pageable)
                .map(categoryMapper::toSummaryResponse);
    }

    public CategoryDetailsResponse getCategory(Authentication authentication, Long id) {
        currentUserProvider.get(authentication);

        return categoryMapper.toDetailsResponse(categoryRepository.findCategory(id)
                .orElseThrow(CategoryNotFoundException::new));
    }
}
