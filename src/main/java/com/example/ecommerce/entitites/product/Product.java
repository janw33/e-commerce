package com.example.ecommerce.entitites.product;

import com.example.ecommerce.entitites.product.enums.*;
import com.example.ecommerce.entitites.product.enums.Color;
import com.example.ecommerce.entitites.product.productEntities.Category;
import com.example.ecommerce.entitites.product.productEntities.Brand;
import com.example.ecommerce.entitites.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 750)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false, length = 3)
    private String currency;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Color color;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Condition condition;

    @ElementCollection(targetClass = Material.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "product_materials",
            joinColumns = @JoinColumn(name = "product_id")
    )
    @Column(name = "material")
    private List<Material> materials;

    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private Size size;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShippingSize shippingSize;

    @Builder.Default
    @Column(nullable = false)
    private Long viewCount = 0L;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.ACTIVE;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;
}
