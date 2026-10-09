package com.example.ecommerce.controllers.product;

import com.example.ecommerce.dtos.product.CreateProductRequest;
import com.example.ecommerce.dtos.product.ProductResponse;
import com.example.ecommerce.dtos.product.UpdateProductRequest;
import com.example.ecommerce.services.product.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @RequestBody @Valid CreateProductRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(request, authentication));
    }

    @PutMapping(path = "/{id}")
    public ProductResponse updateProduct(
            @RequestBody @Valid UpdateProductRequest request,
            @PathVariable Long id,
            Authentication authentication
    ) {
        return productService.updateProduct(request, authentication, id);
    }
}
