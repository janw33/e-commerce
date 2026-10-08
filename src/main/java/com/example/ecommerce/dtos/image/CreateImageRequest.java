package com.example.ecommerce.dtos.image;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateImageRequest {
    @NotBlank(message = "Image url cannot be empty")
    private String url;
}
