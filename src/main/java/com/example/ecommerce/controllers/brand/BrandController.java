package com.example.ecommerce.controllers.brand;

import com.example.ecommerce.dtos.brand.BrandResponse;
import com.example.ecommerce.services.brand.BrandService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/brand")
public class BrandController {
    private final BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @GetMapping(path = "/{categoryId}")
    public Page<BrandResponse> getBrands(
            Authentication authentication,
            @PathVariable Long categoryId,
            @PageableDefault(size = 30) Pageable pageable
    ) {
        return brandService.getBrands(authentication, categoryId, pageable);
    }
}
