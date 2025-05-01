package com.freddypizza.website.entity

import jakarta.persistence.*

@Entity
@Table(name = "order_item")
data class OrderItemEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(nullable = false)
    val quantity: Int,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    val order: OrderEntity,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    val product: ProductEntity,
) {
    override fun toString(): String = "OrderItemEntity(id=$id, quantity=$quantity, productId=${product.id})"
}
