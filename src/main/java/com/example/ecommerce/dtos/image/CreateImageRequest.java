package com.example.ecommerce.dtos.image;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateImageRequest {
    @NotBlank(message = "Image url cannot be empty")
    @Size(min = 2, max = 1000, message = "Image url must be between 2 and 1000 characters")
    private String url;
}
