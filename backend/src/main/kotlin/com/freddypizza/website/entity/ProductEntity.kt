package com.freddypizza.website.entity

import com.freddypizza.website.enums.ProductCategory
import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "products")
data class ProductEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(nullable = false)
    val name: String,
    @Column(columnDefinition = "TEXT")
    val description: String? = null,
    @Column
    val weight: Int? = null,
    @Column
    val ingredients: String? = null,
    @Column(nullable = false)
    val price: BigDecimal,
    @Column(name = "quantity", nullable = false)
    val quantity: Int = 0,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val category: ProductCategory,
    @Column(name = "image_path")
    val imagePath: String? = "/uploads/products/image_placeholder.png",
)
