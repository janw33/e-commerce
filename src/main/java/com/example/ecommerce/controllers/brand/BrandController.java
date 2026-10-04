package com.example.ecommerce.controllers.brand;

import com.example.ecommerce.dtos.brand.BrandResponse;
import com.example.ecommerce.services.brand.BrandService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/brand")
public class BrandController {
    private final BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @GetMapping
    public Page<BrandResponse> getBrands(
            Authentication authentication,
            @RequestParam() Long categoryId,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 30) Pageable pageable
    ) {
        return brandService.getBrands(authentication, categoryId, search, pageable);
    }

    @GetMapping(path = "/{brandId}")
    public BrandResponse getBrand(
            Authentication authentication,
            @PathVariable Long brandId
    ) {
        return brandService.getBrand(authentication, brandId);
    }
}
