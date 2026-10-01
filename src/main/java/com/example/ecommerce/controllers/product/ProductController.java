package com.example.ecommerce.controllers.product;

import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@RestController
//@RequestMapping("api/v1/product")
//public class ProductController {
//    private final ProductService productService;
//
//    @PostMapping
//    public ProductResponse createProduct(
//            @RequestBody @Valid CreateProductRequest request,
//            Authentication authentication
//    ) {
//        return productService.createProduct(request, authentication);
//    }
//}
