package com.example.ecommerce.dtos.image;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageResponse {
    private Long id;
    private String url;
}
