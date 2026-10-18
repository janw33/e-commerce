package com.example.ecommerce.dtos.product;

import com.example.ecommerce.entitites.product.enums.Condition;
import com.example.ecommerce.entitites.product.enums.Size;

import java.math.BigDecimal;

public interface ProductSummaryProjection {
    Long getId();
    String getTitle();
    String getImageUrl();
    BigDecimal getPrice();
    Condition getCondition();
    Size getSize();
    String getBrandName();
    Long getLikeCount();
}
