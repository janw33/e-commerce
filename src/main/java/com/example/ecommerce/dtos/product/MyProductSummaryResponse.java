package com.example.ecommerce.dtos.product;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyProductSummaryResponse {
    private Long id;
    private String imageUrl;
    private long likeCount;
    private long viewCount;
}
