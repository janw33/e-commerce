package com.example.ecommerce.dtos.product;

import com.example.ecommerce.dtos.image.ImageResponse;
import com.example.ecommerce.entitites.product.enums.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;

    //TODO: create SellerResponse
    private Long sellerId;
    private String sellerUsername;

    private Long categoryId;

    private Long brandId;
    private String brandName;

    private String title;
    private String description;

    private BigDecimal price;
    private Currency currency;

    private Set<Color> colors;
    private Condition condition;
    private Set<Material> materials;
    private Size size;
    private ShippingSize shippingSize;

    private Long viewCount;
    private long likeCount;
    private Status status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private LocalDateTime soldAt;

    private List<ImageResponse> images;
}
