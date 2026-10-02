package com.example.ecommerce.services.category;

import com.example.ecommerce.entitites.user.User;
import com.example.ecommerce.providers.user.CurrentUserProvider;
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

    public Page<CategoryReposne> getCategories(Authentication authentication, Pageable pageable) {
        User currentUser = currentUserProvider.get(authentication);

    }
}
