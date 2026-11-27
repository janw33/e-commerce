package com.example.ecommerce;

import com.example.ecommerce.dtos.auth.UserResponse;
import com.example.ecommerce.dtos.brand.BrandResponse;
import com.example.ecommerce.dtos.category.CategoryDetailsResponse;
import com.example.ecommerce.dtos.category.CategorySummaryResponse;
import com.example.ecommerce.dtos.image.CreateImageRequest;
import com.example.ecommerce.dtos.image.ImageResponse;
import com.example.ecommerce.dtos.product.CreateProductRequest;
import com.example.ecommerce.dtos.product.ProductResponse;
import com.example.ecommerce.dtos.product.ProductSummaryResponse;
import com.example.ecommerce.dtos.product.UpdateProductRequest;
import com.example.ecommerce.entitites.product.Product;
import com.example.ecommerce.entitites.product.enums.*;
import com.example.ecommerce.entitites.product.productEntities.Brand;
import com.example.ecommerce.entitites.product.productEntities.Category;
import com.example.ecommerce.entitites.product.productEntities.Image;
import com.example.ecommerce.entitites.user.User;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public final class TestDataUtil {
    private static LocalDateTime testDate() {
        return LocalDateTime.of(1, 1, 1, 1, 1);
    }

    public static User user() {
        return User.builder()
                .id(1L)
                .externalId("1")
                .firstName("test")
                .lastName("test")
                .email("test@email.com")
                .phoneNumber("111-111-111")
                .active(true)
                .createdAt(testDate())
                .updatedAt(testDate())
                .build();
    }

    public static UserResponse userResponse() {
        return UserResponse.builder()
                .id(user().getId())
                .externalId(user().getExternalId())
                .firstName(user().getFirstName())
                .lastName(user().getLastName())
                .email(user().getEmail())
                .phoneNumber(user().getPhoneNumber())
                .build();
    }

    public static Category categoryParent() {
        return Category.builder()
                .id(null)
                .name("testParent")
                .parent(null)
                .sizeType(SizeType.NONE)
                .build();
    }


    public static Category category() {
        return Category.builder()
                .id(1L)
                .name("test")
                .parent(categoryParent())
                .sizeType(SizeType.NONE)
                .build();
    }

    public static CategorySummaryResponse categorySummaryResponse() {
        return CategorySummaryResponse.builder()
                .id(category().getId())
                .name(category().getName())
                .hasChildren(true)
                .build();
    }

    public static CategoryDetailsResponse categoryDetailsResponse() {
        return CategoryDetailsResponse.builder()
                .id(category().getId())
                .name(category().getName())
                .sizeType(category().getSizeType())
                .parentId(category().getParent().getId())
                .hasChildren(true)
                .build();
    }

    public static Brand brand() {
        return Brand.builder()
                .id(1L)
                .name("test")
                .build();
    }

    public static BrandResponse brandResponse() {
        return BrandResponse.builder()
                .id(brand().getId())
                .name(brand().getName())
                .build();
    }

    public static Image image() {
        return Image.builder()
                .id(1L)
                .product(product())
                .url("test")
                .position(0)
                .build();
    }

    public static CreateImageRequest createImageRequest() {
        return new CreateImageRequest(image().getUrl());
    }

    public static ImageResponse imageResponse() {return new ImageResponse(image().getId(), image().getUrl());}

    public static Product product() {
        return Product.builder()
                .id(1L)
                .seller(user())
                .category(category())
                .brand(brand())
                .title("test1")
                .description("test1")
                .price(BigDecimal.TEN)
                .colors(Set.of(Color.BLUE))
                .condition(Condition.NEW)
                .materials(Set.of(Material.COTTON))
                .size(Size.ONE_SIZE)
                .shippingSize(ShippingSize.MEDIUM)
                .viewCount(0L)
                .status(Status.ACTIVE)
                .createdAt(testDate())
                .updatedAt(testDate())
                .deletedAt(null)
                .soldAt(null)
                .build();
    }

    public static Product product2() {
        return Product.builder()
                .id(1L)
                .seller(user())
                .category(category())
                .brand(brand())
                .title("test2")
                .description("test2")
                .price(BigDecimal.ONE)
                .colors(Set.of(Color.GREEN))
                .condition(Condition.LIKE_NEW)
                .materials(Set.of(Material.WOOL))
                .size(Size.ONE_SIZE)
                .shippingSize(ShippingSize.LARGE)
                .viewCount(0L)
                .status(Status.ACTIVE)
                .createdAt(testDate())
                .updatedAt(LocalDateTime.of(1, 1, 1, 1, 2))
                .deletedAt(null)
                .soldAt(null)
                .build();
    }

    public static CreateProductRequest createProductRequest() {
        return CreateProductRequest.builder()
                .title(product().getTitle())
                .description(product().getDescription())
                .categoryId(product().getCategory().getId())
                .brandId(product().getBrand().getId())
                .size(product().getSize())
                .shippingSize(product().getShippingSize())
                .condition(product().getCondition())
                .colors(product().getColors())
                .materials(product().getMaterials())
                .price(product().getPrice())
                .build();
    }

    public static ProductResponse productResponse() {
        return ProductResponse.builder()
                .id(product().getId())
                .sellerId(product().getSeller().getId())
                .sellerUsername(product().getSeller().getFirstName())
                .categoryId(product().getCategory().getId())
                .brandId(product().getBrand().getId())
                .brandName(product().getBrand().getName())
                .title(product().getTitle())
                .description(product().getDescription())
                .price(product().getPrice())
                .colors(product().getColors())
                .condition(product().getCondition())
                .materials(product().getMaterials())
                .size(product().getSize())
                .shippingSize(product().getShippingSize())
                .viewCount(product().getViewCount())
                .likeCount(0)
                .status(product().getStatus())
                .createdAt(testDate())
                .updatedAt(testDate())
                .deletedAt(null)
                .soldAt(null)
                .images(List.of(imageResponse()))
                .build();
    }

    public static UpdateProductRequest updateProductRequest() {
        return UpdateProductRequest.builder()
                .title(product2().getTitle())
                .description(product2().getDescription())
                .categoryId(product2().getCategory().getId())
                .brandId(product2().getBrand().getId())
                .size(product2().getSize())
                .shippingSize(product2().getShippingSize())
                .condition(product2().getCondition())
                .colors(product2().getColors())
                .materials(product2().getMaterials())
                .price(product2().getPrice())
                .build();
    }

    public static ProductSummaryResponse productSummaryResponse() {
        return ProductSummaryResponse.builder()
                .id(product().getId())
                .title(product().getTitle())
                .imageUrl(image().getUrl())
                .price(product().getPrice())
                .condition(product().getCondition())
                .size(product().getSize())
                .brandName(product().getBrand().getName())
                .likeCount(0L)
                .build();
    }

    public static MockMultipartFile imageFile() {
        return new MockMultipartFile(
                "images",
                "test.jpg",
                "image/jpeg",
                new byte[]{1, 2, 3});
    }
}
