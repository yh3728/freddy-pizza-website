package com.freddypizza.website.response.user

import com.freddypizza.website.enums.OrderStatus
import com.freddypizza.website.response.admin.OrderItemResponse
import java.math.BigDecimal
import java.time.LocalDateTime

data class OrderResponse (
    val customerName: String,
    val phone: String,
    val address: String,
    val status: OrderStatus,
    val totalPrice: BigDecimal,
    val comment: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val items: List<OrderItemResponse> = listOf()
)