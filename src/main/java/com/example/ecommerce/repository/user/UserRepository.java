package com.example.ecommerce.repository.user;

import com.example.ecommerce.entitites.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByExternalIdAndActiveTrue(String externalId);

    boolean existsByExternalIdAndActiveTrue(String externalId);
}
