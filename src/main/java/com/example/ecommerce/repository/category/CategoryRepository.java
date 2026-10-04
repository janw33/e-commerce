package com.example.ecommerce.repository.category;

import com.example.ecommerce.dtos.category.DetailsCategoryWithChildrenProjection;
import com.example.ecommerce.dtos.category.SummaryCategoryWithChildrenProjection;
import com.example.ecommerce.entitites.product.productEntities.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("""
    SELECT
        c.id AS id,
        c.name AS name,
        c.sizeType AS sizeType,
        CASE
            WHEN EXISTS (
                SELECT 1
                FROM Category child
                WHERE child.parent.id = c.id
            )
            THEN true
            ELSE false
        END AS hasChildren
    FROM Category c
    WHERE (:parentId IS NULL AND c.parent IS NULL)
       OR (:parentId IS NOT NULL AND c.parent.id = :parentId)
""")
    Page<SummaryCategoryWithChildrenProjection> findCategories(
            @Param("parentId") Long parentId,
            Pageable pageable
    );

    @Query("""
    SELECT
        c.id AS id,
        c.name AS name,
        c.sizeType AS sizeType,
        c.parent.id as parentId,
        CASE
            WHEN EXISTS (
                SELECT 1
                FROM Category child
                WHERE child.parent.id = c.id
            )
            THEN true
            ELSE false
        END AS hasChildren
    FROM Category c
    WHERE c.id = :id
""")
    Optional<DetailsCategoryWithChildrenProjection> findCategory(@Param("id") Long id);
}
