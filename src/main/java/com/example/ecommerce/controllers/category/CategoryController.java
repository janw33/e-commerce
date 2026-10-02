package com.example.ecommerce.controllers.category;

import com.example.ecommerce.services.category.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/category")
public class CategoryController {
    private CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public Page<CategoryReposne> getCategories(
            Authentication authentication,
            Pageable pageable
    ) {
        return categoryService.getCategories(authentication, pageable);
    }
}
