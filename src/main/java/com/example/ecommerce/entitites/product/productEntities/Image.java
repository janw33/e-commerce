package com.example.ecommerce.entitites.product.productEntities;

import com.example.ecommerce.entitites.product.Product;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "images",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"product_id", "position"})
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, length = 1000)
    private String url;

    @Column(nullable = false)
    private int position;
}
