package com.example.ecommerce.entitites.product.productEntities;

import com.example.ecommerce.entitites.product.enums.CategoryGroup;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "brand_category_suggestions",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_brand_category_group",
                        columnNames = {"brand_id", "category_group"}
                )
        }
)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BrandCategorySuggestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryGroup categoryGroup;
}
