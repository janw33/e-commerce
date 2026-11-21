package com.example.ecommerce.repository.like;

import com.example.ecommerce.entitites.product.Product;
import com.example.ecommerce.entitites.product.productEntities.Like;
import com.example.ecommerce.entitites.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

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
}
