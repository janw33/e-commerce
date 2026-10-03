package com.example.ecommerce.mappers.category;

import com.example.ecommerce.dtos.category.CategoryResponse;
import com.example.ecommerce.dtos.category.CategoryWithChildrenProjection;
import com.example.ecommerce.entitites.product.productEntities.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    public CategoryResponse toResponse(CategoryWithChildrenProjection category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .sizeType(category.getSizeType())
                .hasChildren(category.getHasChildren())
                .build();
    }
}
