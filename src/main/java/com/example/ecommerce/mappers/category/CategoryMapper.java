package com.example.ecommerce.mappers.category;

import com.example.ecommerce.dtos.category.CategoryDetailsResponse;
import com.example.ecommerce.dtos.category.CategorySummaryResponse;
import com.example.ecommerce.dtos.category.DetailsCategoryWithChildrenProjection;
import com.example.ecommerce.dtos.category.SummaryCategoryWithChildrenProjection;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    public CategorySummaryResponse toSummaryResponse(SummaryCategoryWithChildrenProjection category) {
        return CategorySummaryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .sizeType(category.getSizeType())
                .hasChildren(category.getHasChildren())
                .build();
    }

    public CategoryDetailsResponse toDetailsResponse(DetailsCategoryWithChildrenProjection category) {
        return CategoryDetailsResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .sizeType(category.getSizeType())
                .parentId(category.getParentId())
                .hasChildren(category.getHasChildren())
                .build();
    }
}
