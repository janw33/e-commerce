package com.example.ecommerce.services.brand;

import com.example.ecommerce.BaseTest.brand.BaseTestBrand;
import com.example.ecommerce.dtos.brand.BrandResponse;
import com.example.ecommerce.exceptions.brand.BrandNotFoundException;
import com.example.ecommerce.exceptions.user.UserNotFoundException;
import com.example.ecommerce.mappers.brand.BrandMapper;
import com.example.ecommerce.providers.CurrentUserProvider;
import com.example.ecommerce.repositories.brand.BrandRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GetBrandTests extends BaseTestBrand {
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
                () -> brandService.getBrand(authentication, brand.getId())
        );

        verify(brandRepository, never()).findById(brand.getId());
    }

    @Test
    void shouldThrowBrandNotFoundExceptionWhenBrandDoesntExists() {
        when(currentUserProvider.get(authentication))
                .thenReturn(user);

        when(brandRepository.findById(brand.getId()))
                .thenReturn(Optional.empty());

        assertThrows(
                BrandNotFoundException.class,
                () -> brandService.getBrand(authentication, brand.getId())
        );

        verify(brandRepository).findById(brand.getId());
    }

    @Test
    void shouldReturnBrand() {
        when(currentUserProvider.get(authentication))
                .thenReturn(user);

        when(brandRepository.findById(brand.getId()))
                .thenReturn(Optional.of(brand));

        BrandResponse result = brandService.getBrand(authentication, brand.getId());

        assertEquals(brand.getId(), result.getId());
        assertEquals(brand.getName(), result.getName());

        verify(brandRepository).findById(brand.getId());
    }
}
