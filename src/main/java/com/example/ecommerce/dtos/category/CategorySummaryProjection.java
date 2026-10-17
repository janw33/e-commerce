package com.example.ecommerce.dtos.category;

public interface CategorySummaryProjection {
    Long getId();
    String getName();
    Long getParentId();
    boolean getHasChildren();
}
