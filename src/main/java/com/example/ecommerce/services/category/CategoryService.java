package com.example.ecommerce.services.category;

import com.example.ecommerce.dtos.category.CategoryDetailsResponse;
import com.example.ecommerce.dtos.category.CategorySummaryResponse;
import com.example.ecommerce.entitites.product.enums.Size;
import com.example.ecommerce.entitites.product.productEntities.Category;
import com.example.ecommerce.exceptions.category.CategoryNotFoundException;
import com.example.ecommerce.mappers.category.CategoryMapper;
import com.example.ecommerce.providers.CurrentUserProvider;
import com.example.ecommerce.repository.category.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

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

    public List<Size> getSizes(Long categoryId, Authentication authentication) {
        currentUserProvider.get(authentication);

        if (categoryId == null) {
            return List.of(Size.values());
        }

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(CategoryNotFoundException::new);

        return Stream.of(Size.values())
                .filter(size -> size.getType() == category.getSizeType())
                .toList();
    }
}
