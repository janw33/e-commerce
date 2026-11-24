package com.example.ecommerce.services.product;

import com.example.ecommerce.dtos.product.*;
import com.example.ecommerce.entitites.product.Product;
import com.example.ecommerce.entitites.product.enums.*;
import com.example.ecommerce.entitites.product.productEntities.Brand;
import com.example.ecommerce.entitites.product.productEntities.Category;
import com.example.ecommerce.entitites.product.productEntities.Image;
import com.example.ecommerce.entitites.user.User;
import com.example.ecommerce.exceptions.brand.BrandNotFoundException;
import com.example.ecommerce.exceptions.category.CategoryNotFoundException;
import com.example.ecommerce.exceptions.image.InvalidImageCountException;
import com.example.ecommerce.exceptions.product.InvalidSizeTypeException;
import com.example.ecommerce.exceptions.product.InvalidStatusException;
import com.example.ecommerce.exceptions.product.ProductNotFoundException;
import com.example.ecommerce.mappers.image.ImageMapper;
import com.example.ecommerce.mappers.product.ProductMapper;
import com.example.ecommerce.providers.CurrentUserProvider;
import com.example.ecommerce.repository.brand.BrandRepository;
import com.example.ecommerce.repository.category.CategoryRepository;
import com.example.ecommerce.repository.image.ImageRepository;
import com.example.ecommerce.repository.like.LikeRepository;
import com.example.ecommerce.repository.product.ProductRepository;
import com.example.ecommerce.services.image.ImageUploadValidator;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
    private final ImageUploadValidator imageUploadValidator;

    private static final int MAX_IMAGES_PER_PRODUCT = 20;

    public ProductService(CurrentUserProvider currentUserProvider, ProductRepository productRepository, ImageRepository imageRepository,
                          CategoryRepository categoryRepository, BrandRepository brandRepository, LikeRepository likeRepository,
                          ProductMapper productMapper, ImageMapper imageMapper, ImageUploadValidator imageUploadValidator) {
        this.currentUserProvider = currentUserProvider;
        this.productRepository = productRepository;
        this.imageRepository = imageRepository;
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
        this.likeRepository = likeRepository;
        this.productMapper = productMapper;
        this.imageMapper = imageMapper;
        this.imageUploadValidator = imageUploadValidator;
    }

    @Transactional(rollbackOn = IOException.class)
    public ProductResponse createProduct(CreateProductRequest request, List<MultipartFile> images, Authentication authentication) throws IOException {
        User currentUser = currentUserProvider.get(authentication);

        if (images.isEmpty() || images.size() > MAX_IMAGES_PER_PRODUCT) {
            throw new InvalidImageCountException();
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(CategoryNotFoundException::new);

        if (category.getSizeType() != request.getSize().getType()) {
            throw new InvalidSizeTypeException();
        }

        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(BrandNotFoundException::new);

        Product newProduct = productMapper.toEntity(request, currentUser, category, brand);

        Product savedProduct = productRepository.save(newProduct);

        List<Image> imageRecords = new ArrayList<>();

        List<String> storedFileNames = new ArrayList<>();
        imageUploadValidator.deleteFilesOnRollback(storedFileNames);

        for (int i = 0; i < images.size(); i++) {
            MultipartFile file = images.get(i);
            String fileName = imageUploadValidator.store(file);
            storedFileNames.add(fileName);

            Image image = Image.builder()
                    .product(savedProduct)
                    .url("/api/v1/image/files/" + fileName)
                    .position(i)
                    .build();

            imageRecords.add(image);
        }

        List<Image> savedImages = imageRepository.saveAll(imageRecords);

        return productMapper.toResponse(savedProduct, 0, savedImages.stream().map(imageMapper::toResponse).toList());
    }

    @Transactional
    public ProductResponse updateProduct(UpdateProductRequest request, Authentication authentication, Long id) {
        User currentUser = currentUserProvider.get(authentication);

        Product product = productRepository.findBySellerAndIdAndStatus(currentUser, id, Status.ACTIVE)
                .orElseThrow(ProductNotFoundException::new);

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(CategoryNotFoundException::new);

        if (category.getSizeType() != request.getSize().getType()) {
            throw new InvalidSizeTypeException();
        }

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
        product.setShippingSize(request.getShippingSize());

        long likeCount = likeRepository.countByProductId(product.getId());

        imageRepository.deleteAllByProductId(product.getId());
        imageRepository.flush();

        List<Image> newImages = IntStream.range(0, request.getImages().size())
                .mapToObj(i -> imageMapper.toEntity(
                        request.getImages().get(i),
                        product,
                        i))
                .toList();

        List<Image> savedImages = imageRepository.saveAll(newImages);

        return productMapper.toResponse(product, likeCount, savedImages.stream().map(imageMapper::toResponse).toList());
    }

    public Page<ProductSummaryResponse> getProducts(
            Long categoryId, Size size, Long brandId,
            Condition condition, Set<Color> colors, BigDecimal minPrice,
            BigDecimal maxPrice, Set<Material> materials, ProductSortBy sortBy,
            String search, Authentication authentication, Pageable pageable) {

        currentUserProvider.get(authentication);

        return productRepository.getProducts(categoryId, size, brandId, condition, colors, minPrice, maxPrice, materials, sortBy.name(), search, pageable)
                .map(productMapper::toSummaryResponse);
    }

    public ProductResponse getProduct(Long id, Authentication authentication) {
        currentUserProvider.get(authentication);

        Product product = productRepository.findByIdAndStatusIn(id, List.of(Status.ACTIVE, Status.SOLD))
                        .orElseThrow(ProductNotFoundException::new);

        long likeCount = likeRepository.countByProductId(id);

        List<Image> images = imageRepository.findAllByProductIdOrderByPositionAsc(id);

        return productMapper.toResponse(product, likeCount, images.stream().map(imageMapper::toResponse).toList());
    }

    public Page<MyProductSummaryResponse> getMyProducts(Authentication authentication, Status status, Pageable pageable) {
        User currentUser = currentUserProvider.get(authentication);

        if (status == Status.DELETED) {
            throw new InvalidStatusException(); }

        return productRepository.getMyProducts(currentUser, status, pageable)
                .map(productMapper::toMySummaryResponse);
    }

    public Page<ProductSummaryResponse> getSellerProducts(Authentication authentication, Long sellerId,ProductSortBy sortBy, Pageable pageable) {
        currentUserProvider.get(authentication);

        return productRepository.getBySellerId(sellerId, sortBy.name(), pageable)
                .map(productMapper::toSummaryResponse);
    }

    public Page<ProductSummaryResponse> getLikedProducts(Authentication authentication, Pageable pageable) {
        User currentUser = currentUserProvider.get(authentication);

        return likeRepository.findAllLikedProductsByUserId(currentUser.getId(), pageable)
                .map(productMapper::toSummaryResponse);
    }

    @Transactional
    public void deleteProduct(Long id, Authentication authentication) {
        User currentUser = currentUserProvider.get(authentication);

        Product product = productRepository.findBySellerAndIdAndStatusIn(currentUser, id, List.of(Status.ACTIVE, Status.SOLD))
                .orElseThrow(ProductNotFoundException::new);

        imageRepository.deleteAllByProductId(product.getId());

        product.setStatus(Status.DELETED);
        product.setDeletedAt(LocalDateTime.now());
    }

}
