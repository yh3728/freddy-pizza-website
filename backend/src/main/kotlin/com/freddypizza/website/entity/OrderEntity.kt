package com.freddypizza.website.entity

import com.freddypizza.website.enums.OrderStatus
import com.freddypizza.website.enums.PaymentType
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "orders")
data class OrderEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(name = "customer_name", nullable = false)
    val customerName: String,
    @Column(nullable = false)
    val phone: String,
    @Column(nullable = false)
    val address: String,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: OrderStatus = OrderStatus.NEW,
    @Column(name = "total_price", nullable = false)
    var totalPrice: BigDecimal = BigDecimal.ZERO,
    @Column(columnDefinition = "TEXT")
    val comment: String? = null,
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @Column(name = "tracking_code", unique = true, nullable = false)
    val trackingCode: String,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val payment: PaymentType,
    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL], orphanRemoval = true)
    var items: MutableList<OrderItemEntity> = mutableListOf(),
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_delivery_id")
    var assignedDelivery: StaffEntity? = null,
)
