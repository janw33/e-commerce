package com.example.ecommerce.dtos.category;

import com.example.ecommerce.entitites.product.enums.SizeType;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategorySummaryResponse {
    private Long id;
    private String name;
    private Long parentId;
    private boolean hasChildren;
}
