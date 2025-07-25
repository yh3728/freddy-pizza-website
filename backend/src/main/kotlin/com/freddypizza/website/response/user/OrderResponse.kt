package com.freddypizza.website.response.user

import com.freddypizza.website.enums.OrderStatus
import com.freddypizza.website.enums.PaymentType
import com.freddypizza.website.response.admin.OrderItemResponse
import java.math.BigDecimal
import java.time.LocalDateTime

data class OrderResponse(
    val status: OrderStatus,
    val totalPrice: BigDecimal,
    val comment: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val items: List<OrderItemResponse> = listOf(),
    val payment: PaymentType,
    val trackingCode: String
)
