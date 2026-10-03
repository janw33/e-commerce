package com.example.ecommerce.dtos.category;

import com.example.ecommerce.entitites.product.enums.SizeType;

public interface DetailsCategoryWithChildrenProjection {
    Long getId();
    String getName();
    SizeType getSizeType();
    Long getParentId();
    boolean getHasChildren();
}
