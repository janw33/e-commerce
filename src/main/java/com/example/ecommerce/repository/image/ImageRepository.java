package com.example.ecommerce.repository.image;

import com.example.ecommerce.entitites.product.productEntities.Image;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findAllByProductIdOrderByPositionAsc(Long productId);
}
