package com.example.ecommerce.dtos.product;

import com.example.ecommerce.dtos.image.CreateImageRequest;
import com.example.ecommerce.entitites.product.enums.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductRequest {
    @Valid
    @NotEmpty(message = "Images cannot be empty")
    @Size(max = 20, message = "Number of images must not exceed 20")
    private List<CreateImageRequest> images;

    @NotBlank(message = "Title cannot be blank")
    @Size(min = 5, max = 100, message = "Title must be between 5 and 100 characters")
    private String title;

    @NotBlank(message = "Description cannot be blank")
    @Size(min = 5, max = 750, message = "Description must be between 5 and 750 characters")
    private String description;

    @NotNull(message = "Category id cannot be null")
    private Long categoryId;

    @NotNull(message = "Brand id cannot be null")
    private Long brandId;

    @NotNull(message = "Size cannot be null")
    private com.example.ecommerce.entitites.product.enums.Size size;

    @NotNull(message = "Condition cannot be null")
    private Condition condition;

    @NotEmpty(message = "Colors cannot be empty")
    @Size(max = 2, message = "Number of colors must not exceed 2")
    private Set<Color> colors;

    private Set<Material> materials;

    @NotNull(message = "Price cannot be null")
    @Positive(message = "Price must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Price must have at most 8 integer digits and 2 decimal places")
    private BigDecimal price;

    @NotNull(message = "Shipping size cannot be null")
    private ShippingSize shippingSize;
}
