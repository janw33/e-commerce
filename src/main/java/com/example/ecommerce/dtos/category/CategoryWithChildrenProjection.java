package com.example.ecommerce.dtos.category;

import com.example.ecommerce.entitites.product.enums.SizeType;

public interface CategoryWithChildrenProjection {
    Long getId();
    String getName();
    SizeType getSizeType();
    boolean getHasChildren();
}
