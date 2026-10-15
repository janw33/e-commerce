package com.example.ecommerce.controllers.product;

import com.example.ecommerce.dtos.product.*;
import com.example.ecommerce.entitites.product.enums.*;
import com.example.ecommerce.services.product.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Set;

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

    @GetMapping
    public Page<ProductSummaryResponse> getProducts(
            @RequestParam (required = false) Long categoryId,
            @RequestParam (required = false) Size size,
            @RequestParam (required = false) Long brandId,
            @RequestParam (required = false) Condition condition,
            @RequestParam (required = false) Set<Color> colors,
            @RequestParam (required = false) BigDecimal minPrice,
            @RequestParam (required = false) BigDecimal maxPrice,
            @RequestParam (required = false) Set<Material> materials,
            @RequestParam (defaultValue = "RELEVANCE", required = false) ProductSortBy sortBy,
            @RequestParam (required = false) String search,
            Authentication authentication,
            @PageableDefault(size = 100) Pageable pageable
    ) {
        return productService.getProducts(categoryId, size, brandId, condition, colors, minPrice, maxPrice, materials, sortBy, search,authentication, pageable);
    }

    @GetMapping(path = "/{id}")
    public ProductResponse getProduct(
            @PathVariable Long id,
            Authentication authentication
    ) {
        return productService.getProduct(id, authentication);
    }

    @GetMapping(path = "/my")
    public Page<MyProductSummaryResponse> getMyProducts(
            Authentication authentication,
            @RequestParam (defaultValue = "ACTIVE", required = false) Status status,
            @PageableDefault(size = 30) Pageable pageable
    ) {
        return productService.getMyProducts(authentication, status, pageable);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long id,
            Authentication authentication
    ) {
        productService.deleteProduct(id, authentication);
        return ResponseEntity.noContent().build();
    }
}
