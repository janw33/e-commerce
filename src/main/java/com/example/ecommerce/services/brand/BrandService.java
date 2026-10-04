package com.example.ecommerce.services.brand;

import com.example.ecommerce.dtos.brand.BrandResponse;
import com.example.ecommerce.exceptions.brand.BrandNotFoundException;
import com.example.ecommerce.mappers.brand.BrandMapper;
import com.example.ecommerce.providers.CurrentUserProvider;
import com.example.ecommerce.repository.brand.BrandRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class BrandService {
    private final CurrentUserProvider currentUserProvider;
    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    public BrandService(CurrentUserProvider currentUserProvider, BrandRepository brandRepository, BrandMapper brandMapper) {
        this.currentUserProvider = currentUserProvider;
        this.brandRepository = brandRepository;
        this.brandMapper = brandMapper;
    }

    public Page<BrandResponse> getBrands(Authentication authentication, Long categoryId, String search, Pageable pageable) {
        currentUserProvider.get(authentication);

        return brandRepository.findAllByCategoryId(categoryId, search, pageable).map(brandMapper::toResponse);
    }

    public BrandResponse getBrand(Authentication authentication, Long brandId) {
        currentUserProvider.get(authentication);

        return brandMapper.toResponse(brandRepository.findById(brandId)
                .orElseThrow(BrandNotFoundException::new));
    }
}
