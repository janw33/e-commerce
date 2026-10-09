package com.example.ecommerce.services.product;

import com.example.ecommerce.dtos.product.CreateProductRequest;
import com.example.ecommerce.dtos.product.ProductResponse;
import com.example.ecommerce.dtos.product.UpdateProductRequest;
import com.example.ecommerce.entitites.product.Product;
import com.example.ecommerce.entitites.product.productEntities.Brand;
import com.example.ecommerce.entitites.product.productEntities.Category;
import com.example.ecommerce.entitites.product.productEntities.Image;
import com.example.ecommerce.entitites.user.User;
import com.example.ecommerce.exceptions.brand.BrandNotFoundException;
import com.example.ecommerce.exceptions.category.CategoryNotFoundException;
import com.example.ecommerce.exceptions.product.ProductNotFoundException;
import com.example.ecommerce.mappers.image.ImageMapper;
import com.example.ecommerce.mappers.product.ProductMapper;
import com.example.ecommerce.providers.CurrentUserProvider;
import com.example.ecommerce.repository.brand.BrandRepository;
import com.example.ecommerce.repository.category.CategoryRepository;
import com.example.ecommerce.repository.image.ImageRepository;
import com.example.ecommerce.repository.like.LikeRepository;
import com.example.ecommerce.repository.product.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

@Service
public class ProductService {
    private final CurrentUserProvider currentUserProvider;
    private final ProductRepository productRepository;
    private final ImageRepository imageRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final LikeRepository likeRepository;
    private final ProductMapper productMapper;
    private final ImageMapper imageMapper;

    public ProductService(CurrentUserProvider currentUserProvider, ProductRepository productRepository, ImageRepository imageRepository,
                          CategoryRepository categoryRepository, BrandRepository brandRepository, LikeRepository likeRepository,
                          ProductMapper productMapper, ImageMapper imageMapper) {
        this.currentUserProvider = currentUserProvider;
        this.productRepository = productRepository;
        this.imageRepository = imageRepository;
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
        this.likeRepository = likeRepository;
        this.productMapper = productMapper;
        this.imageMapper = imageMapper;
    }

    @Transactional
    public ProductResponse createProduct(CreateProductRequest request, Authentication authentication) {
        User currentUser = currentUserProvider.get(authentication);

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(CategoryNotFoundException::new);

        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(BrandNotFoundException::new);

        Product newProduct = productMapper.toEntity(request, currentUser, category, brand);

        Product savedProduct = productRepository.save(newProduct);

        List<Image> images = imageRepository.saveAll(
                IntStream.range(0, request.getImages().size())
                        .mapToObj(i -> imageMapper.toEntity(
                                request.getImages().get(i), savedProduct, i))
                        .toList());

        return productMapper.toResponse(savedProduct, 0, images.stream().map(imageMapper::toResponse).toList());
    }

    @Transactional
    public ProductResponse updateProduct(UpdateProductRequest request, Authentication authentication, Long id) {
        User currentUser = currentUserProvider.get(authentication);

        Product product = productRepository.findBySellerAndIdAndStatusActive(currentUser, id)
                .orElseThrow(ProductNotFoundException::new);

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(CategoryNotFoundException::new);

        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(BrandNotFoundException::new);

        product.setTitle(request.getTitle());
        product.setDescription(request.getDescription());
        product.setCategory(category);
        product.setBrand(brand);
        product.setSize(request.getSize());
        product.setCondition(request.getCondition());
        product.setColors(request.getColors());
        product.setMaterials(request.getMaterials());
        product.setPrice(request.getPrice());
        product.setCurrency(request.getCurrency());
        product.setShippingSize(request.getShippingSize());

        long likeCount = likeRepository.countByProductId(product.getId());
        imageRepository.deleteAllByProductId(product.getId());

        List<Image> newImages = IntStream.range(0, request.getImages().size())
                .mapToObj(i -> imageMapper.toEntity(
                        request.getImages().get(i),
                        product,
                        i))
                .toList();

        List<Image> savedImages = imageRepository.saveAll(newImages);

        return productMapper.toResponse(product, likeCount, savedImages.stream().map(imageMapper::toResponse).toList());
    }
}
