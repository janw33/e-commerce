package com.example.ecommerce.repository.brand;

import com.example.ecommerce.entitites.product.productEntities.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
    @Query(
            value = """
            SELECT b
            FROM Brand b
            JOIN Product p ON p.brand = b
            WHERE p.category.id = :categoryId
            AND b.name LIKE '%' || COALESCE(:search, '') || '%'
            GROUP BY b.id, b.name
            ORDER BY COUNT(p.id) DESC
        """,
            countQuery = """
                        SELECT COUNT(DISTINCT b.id)
                        FROM Brand b
                        JOIN Product p ON p.brand = b
                        WHERE p.category.id = :categoryId
                        AND b.name LIKE '%' || COALESCE(:search, '') || '%'
                    """
    )
    Page<Brand> findAllByCategoryId(
            @Param("categoryId") Long categoryId,
            @Param("search") String search,
            Pageable pageable
    );
}