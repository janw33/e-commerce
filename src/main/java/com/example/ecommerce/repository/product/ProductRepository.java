package com.example.ecommerce.repository.product;

import com.example.ecommerce.entitites.product.Product;
import com.example.ecommerce.entitites.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByUserAndIdAndStatusActive(User currentUser, Long id);

}
