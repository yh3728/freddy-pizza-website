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
    @Column(nullable = false)
    val price: BigDecimal,
    @Column(name = "is_available", nullable = false)
    val isAvailable: Boolean = false,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val category: ProductCategory = ProductCategory.OTHER,
)
