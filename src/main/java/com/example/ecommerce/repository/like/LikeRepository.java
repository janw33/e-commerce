package com.example.ecommerce.repository.like;

import com.example.ecommerce.dtos.product.ProductSummaryProjection;
import com.example.ecommerce.entitites.product.Product;
import com.example.ecommerce.entitites.product.productEntities.Like;
import com.example.ecommerce.entitites.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.stream.IntStream;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    long countByProductId(Long id);

    boolean existsByUserAndProduct(User currentUser, Product product);

    @Modifying
    @Query(
            value = """
    INSERT INTO likes (user_id, product_id)
    VALUES (:userId, :productId)
    ON CONFLICT (user_id, product_id) DO NOTHING
  """, nativeQuery = true)

    int insertIfAbsent(
            @Param("userId") Long userId,
            @Param("productId") Long productId
    );

    @Modifying
    @Query("""
        delete from Like l
        where l.user.id = :userId and l.product.id = :productId
        """)
    int deleteLike(@Param("userId") Long userId, @Param("productId") Long productId);

    @Query (value = """
        SELECT p.id AS id,
               p.title AS title,
               i.url AS imageUrl,
               p.price AS price,
               p.size AS size,
               p.condition AS condition,
               p.brand.name AS brandName,
               COUNT(allLikes.id) AS likeCount
        FROM Like liked
        JOIN liked.product p
        JOIN Like allLikes ON allLikes.product = p
        LEFT JOIN Image i ON i.product = p AND i.position = (
            SELECT MIN(i2.position) FROM Image i2 WHERE i2.product = p
        )
        WHERE liked.user.id = :userId
          AND p.status IN (ACTIVE, SOLD)
        GROUP BY p.id, p.title, i.url, p.price, p.size, p.condition,
                 p.brand.name, liked.createdAt, liked.id
        ORDER BY liked.createdAt DESC, liked.id DESC
        """, countQuery = """
        SELECT COUNT(liked)
        FROM Like liked
        JOIN liked.product p
        WHERE liked.user.id = :userId
          AND p.status IN (ACTIVE, SOLD)
        """)
    Page<ProductSummaryProjection> findAllLikedProductsByUserId(@Param("userId") Long userId, Pageable pageable);
}
