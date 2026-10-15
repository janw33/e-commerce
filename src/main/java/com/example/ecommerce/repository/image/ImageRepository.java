package com.example.ecommerce.repository.image;

import com.example.ecommerce.entitites.product.productEntities.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findAllByProductIdOrderByPositionAsc(Long productId);

    void deleteAllByProductId(Long id);

    Optional<Image> findFirstByProductIdOrderByPositionAsc(Long id);
}
