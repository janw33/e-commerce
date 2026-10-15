package com.example.ecommerce.dtos.product;

import com.example.ecommerce.entitites.product.enums.Condition;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductSummaryResponse {
    private Long id;
    private String title;
    private String imageUrl;
    private BigDecimal price;
    private Condition condition;
    private String brandName;
    private long likeCount;
}
