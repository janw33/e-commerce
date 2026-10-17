package com.example.ecommerce.controllers.category;

import com.example.ecommerce.dtos.category.CategoryDetailsResponse;
import com.example.ecommerce.dtos.category.CategorySummaryResponse;
import com.example.ecommerce.entitites.product.enums.Condition;
import com.example.ecommerce.entitites.product.enums.Size;
import com.example.ecommerce.services.category.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            Pageable pageable
    ) {
        return categoryService.getCategories(authentication, pageable);
    }

    @GetMapping(path = "/{id}")
    public CategoryDetailsResponse getCategory(
            Authentication authentication,
            @PathVariable Long id
    ) {
        return categoryService.getCategory(authentication, id);
    }

    @GetMapping("/sizes")
    public List<Size> getSizes(
            @RequestParam(required = false) Long categoryId,
            Authentication authentication
    ) {
        return categoryService.getSizes(categoryId, authentication);
    }

    @GetMapping("/conditions")
    public List<Condition> getConditions(
            Authentication authentication
    ) {
        return categoryService.getConditions(authentication);
    }
}
