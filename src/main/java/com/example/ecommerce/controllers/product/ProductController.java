package com.example.ecommerce.controllers.product;

import com.example.ecommerce.dtos.product.*;
import com.example.ecommerce.entitites.product.enums.*;
import com.example.ecommerce.services.product.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("api/v1/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestPart("product") CreateProductRequest request,
            @RequestPart("images") List<MultipartFile> images,
            Authentication authentication
    ) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.createProduct(request, images, authentication));
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
            @RequestParam(name = "productSize", required = false) Size size,
            @RequestParam (required = false) Long brandId,
            @RequestParam (required = false) Condition condition,
            @RequestParam (required = false) Set<Color> colors,
            @RequestParam (required = false) BigDecimal minPrice,
            @RequestParam (required = false) BigDecimal maxPrice,
            @RequestParam (required = false) Set<Material> materials,
            @RequestParam (defaultValue = "RELEVANCE", required = false) ProductSortBy sortBy,
            @RequestParam (required = false, defaultValue = "") String search,
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
            @PageableDefault(size = 100) Pageable pageable
    ) {
        return productService.getMyProducts(authentication, status, pageable);
    }

    @GetMapping(path = "/seller")
    public Page<ProductSummaryResponse> getSellerProducts(
            Authentication authentication,
            @RequestParam() Long sellerId,
            @RequestParam (defaultValue = "NEWEST", required = false) ProductSortBy sortBy,
            @PageableDefault(size = 100) Pageable pageable
    ) {
        return productService.getSellerProducts(authentication, sellerId, sortBy, pageable);
    }

    @GetMapping(path = "/liked")
    public Page<ProductSummaryResponse> getLikedProducts(
            Authentication authentication,
            @PageableDefault(size = 100) Pageable pageable
    ) {
        return productService.getLikedProducts(authentication, pageable);
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
