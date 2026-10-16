package com.example.ecommerce.controllers.category;

import com.example.ecommerce.dtos.category.CategoryDetailsResponse;
import com.example.ecommerce.dtos.category.CategorySummaryResponse;
import com.example.ecommerce.services.category.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/category")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public Page<CategorySummaryResponse> getCategories(
            Authentication authentication,
            @RequestParam(required = false) Long parentId,
            Pageable pageable
    ) {
        return categoryService.getCategories(authentication, parentId, pageable);
    }

    @GetMapping(path = "/{id}")
    public CategoryDetailsResponse getCategory(
            Authentication authentication,
            @PathVariable Long id
    ) {
        return categoryService.getCategory(authentication, id);
    }
}
