package com.example.ecommerce.repository.like;

import com.example.ecommerce.entitites.product.productEntities.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    long countByProductId(Long id);
}
