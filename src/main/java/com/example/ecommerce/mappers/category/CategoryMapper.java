package com.example.ecommerce.mappers.category;

import com.example.ecommerce.dtos.category.CategoryDetailsResponse;
import com.example.ecommerce.dtos.category.CategorySummaryResponse;
import com.example.ecommerce.dtos.category.CategoryDetailsProjection;
import com.example.ecommerce.dtos.category.CategorySummaryProjection;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    public CategorySummaryResponse toSummaryResponse(CategorySummaryProjection category) {
        return CategorySummaryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .hasChildren(category.getHasChildren())
                .build();
    }

    public CategoryDetailsResponse toDetailsResponse(CategoryDetailsProjection category) {
        return CategoryDetailsResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .sizeType(category.getSizeType())
                .parentId(category.getParentId())
                .hasChildren(category.getHasChildren())
                .build();
    }
}
