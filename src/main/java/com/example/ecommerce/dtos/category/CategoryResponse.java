package com.example.ecommerce.dtos.category;

import com.example.ecommerce.entitites.product.enums.SizeType;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponse {
    private Long id;
    private String name;
    private SizeType sizeType;
    private boolean hasChildren;
}
