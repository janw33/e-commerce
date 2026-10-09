package com.example.ecommerce.repository.product;

import com.example.ecommerce.entitites.product.Product;
import com.example.ecommerce.entitites.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query ("SELECT p FROM Product p WHERE p.seller = :currentUser AND p.id = :id AND p.status = 'ACTIVE'")
    Optional<Product> findBySellerAndIdAndStatusActive(@Param("currentUser") User currentUser, @Param("id") Long id);

}
