package com.example.ecommerce.repository.product;

import com.example.ecommerce.dtos.product.MyProductSummaryProjection;
import com.example.ecommerce.dtos.product.ProductSummaryProjection;
import com.example.ecommerce.entitites.product.Product;
import com.example.ecommerce.entitites.product.enums.*;
import com.example.ecommerce.entitites.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query(
            value = """
        SELECT
            p.id AS id,
            p.title AS title,
            i.url AS imageUrl,
            p.price AS price,
            p.condition AS condition,
            p.brand.name AS brandName,
            COUNT(DISTINCT l.id) AS likeCount
        FROM Product p
        LEFT JOIN Like l ON l.product = p
        LEFT JOIN Image i ON i.product = p
        WHERE p.status = 'ACTIVE'
          AND (:categoryId IS NULL OR p.category.id = :categoryId)
          AND (:size IS NULL OR p.size = :size)
          AND (:brandId IS NULL OR p.brand.id = :brandId)
          AND (:condition IS NULL OR p.condition = :condition)
          AND (
              :colors IS NULL OR EXISTS (
                  SELECT 1
                  FROM Product p2
                  JOIN p2.colors c
                  WHERE p2 = p
                  AND c IN :colors
              )
          )
          AND (:minPrice IS NULL OR p.price >= :minPrice)
          AND (:maxPrice IS NULL OR p.price <= :maxPrice)
          AND (
              :materials IS NULL OR EXISTS (
                  SELECT 1
                  FROM Product p2
                  JOIN p2.materials m
                  WHERE p2 = p
                    AND m IN :materials
              )
          )
          AND LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%'))
          AND i.position = (
              SELECT MIN(i2.position)
              FROM Image i2
              WHERE i2.product = p
          )
        GROUP BY
            p.id,
            p.title,
            i.url,
            p.price,
            p.condition,
            p.brand.name,
            p.createdAt,
            p.viewCount
        ORDER BY
            CASE WHEN :sortBy = 'PRICE_ASC'
                 THEN p.price END ASC,
            CASE WHEN :sortBy = 'PRICE_DESC'
                 THEN p.price END DESC,
            CASE WHEN :sortBy = 'NEWEST'
                 THEN p.createdAt END DESC,
            CASE WHEN :sortBy = 'RELEVANCE'
                 THEN COUNT(DISTINCT l.id) * 3 + p.viewCount END DESC
        """, countQuery = """
        SELECT COUNT(p)
        FROM Product p
        WHERE p.status = 'ACTIVE'
          AND (:categoryId IS NULL OR p.category.id = :categoryId)
          AND (:size IS NULL OR p.size = :size)
          AND (:brandId IS NULL OR p.brand.id = :brandId)
          AND (:condition IS NULL OR p.condition = :condition)
          AND (
              :colors IS NULL OR EXISTS (
                  SELECT 1
                  FROM Product p2
                  JOIN p2.colors c
                  WHERE p2 = p
                  AND c IN :colors
              )
          )
          AND (:minPrice IS NULL OR p.price >= :minPrice)
          AND (:maxPrice IS NULL OR p.price <= :maxPrice)
          AND (
              :materials IS NULL OR EXISTS (
                  SELECT 1
                  FROM Product p2
                  JOIN p2.materials m
                  WHERE p2 = p
                    AND m IN :materials
              )
          )
          AND LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%'))
        """
    )
    Page<ProductSummaryProjection> findProducts(
            @Param("categoryId") Long categoryId,
            @Param("size") Size size,
            @Param("brandId") Long brandId,
            @Param("condition") Condition condition,
            @Param("colors") Set<Color> colors,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("materials") Set<Material> materials,
            @Param("sortBy") String sortBy,
            @Param("search") String search,
            Pageable pageable
    );

    @Query (
            value = """
           SELECT 
                p.id as id,
                i.url as imageUrl,
                p.viewCount as viewCount,
                COUNT(DISTINCT l.id) as likeCount
           FROM Product p
           LEFT JOIN Like l ON p = l.product
           LEFT JOIN Image i ON p = i.product
           WHERE p.seller = :currentUser
           AND (p.status = :status)
           AND i.position = (
              SELECT MIN(i2.position)
              FROM Image i2
              WHERE i2.product = p)
           GROUP BY 
               p.id,
               i.url,
               p.viewCount,
               p.createdAt
           ORDER BY p.createdAt DESC
           """, countQuery = """
            SELECT COUNT(p)
            FROM Product p
            WHERE p.seller = :currentUser
           AND (:status IS NULL OR p.status = :status)
           """
    )
    Page<MyProductSummaryProjection> findBySellerAndStatus(@Param("currentUser") User currentUser, @Param("status") Status status, Pageable pageable);

    Optional<Product> findByIdAndStatus(Long id, Status status);

    Optional<Product> findBySellerAndIdAndStatus(User currentUser, Long id, Status status);

    Optional<Product> findByIdAndStatusIn(Long id, Collection<Status> statuses);

    Optional<Product> findBySellerAndIdAndStatusIn(User currentUser, Long id, Collection<Status> statuses);
}
