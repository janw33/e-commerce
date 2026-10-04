package com.example.ecommerce.services.brand;

import com.example.ecommerce.BaseTest.brand.BaseTestBrand;
import com.example.ecommerce.dtos.brand.BrandResponse;
import com.example.ecommerce.dtos.category.CategorySummaryResponse;
import com.example.ecommerce.entitites.product.productEntities.Brand;
import com.example.ecommerce.exceptions.user.UserNotFoundException;
import com.example.ecommerce.mappers.brand.BrandMapper;
import com.example.ecommerce.mappers.category.CategoryMapper;
import com.example.ecommerce.providers.CurrentUserProvider;
import com.example.ecommerce.repositories.brand.BrandRepository;
import com.example.ecommerce.repositories.category.CategoryRepository;
import com.example.ecommerce.services.category.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GetBrandsTests extends BaseTestBrand {
    private final BrandMapper brandMapper = new BrandMapper();

    @Mock
    private CurrentUserProvider currentUserProvider;

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private Authentication authentication;

    private BrandService brandService;

    @BeforeEach
    void setUp() {
        brandService = new BrandService(
                currentUserProvider,
                brandRepository,
                brandMapper
        );
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenUserDoesntExists() {
        when(currentUserProvider.get(authentication))
                .thenThrow(new UserNotFoundException());

        assertThrows(
                UserNotFoundException.class,
                () -> brandService.getBrands(authentication, category.getId(), Pageable.unpaged())
        );

        verify(brandRepository, never()).findAllByCategoryId(category.getId(), Pageable.unpaged());
    }

    @Test
    void shouldReturnBrands() {
        when(currentUserProvider.get(authentication))
                .thenReturn(user);

        Page<Brand> page =  new PageImpl<>(List.of(brand));

        when(brandRepository.findAllByCategoryId(category.getId(), Pageable.unpaged()))
               .thenReturn(page);

        Page<BrandResponse> result = brandService.getBrands(authentication, category.getId(), Pageable.unpaged());

        assertEquals(1, result.getTotalElements());

        BrandResponse first = result.getContent().getFirst();

        assertEquals(brand.getId(), first.getId());
        assertEquals(brand.getName(), first.getName());

        verify(brandRepository).findAllByCategoryId(category.getId(), Pageable.unpaged());
    }
}
