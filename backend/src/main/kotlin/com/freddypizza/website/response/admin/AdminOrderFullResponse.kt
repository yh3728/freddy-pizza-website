package com.freddypizza.website.response.admin

import com.freddypizza.website.enums.OrderStatus
import com.freddypizza.website.enums.PaymentType
import java.math.BigDecimal
import java.time.LocalDateTime

data class AdminOrderFullResponse(
    val id: Long,
    val customerName: String,
    val phone: String,
    val address: String,
    val status: OrderStatus,
    val totalPrice: BigDecimal,
    val comment: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val payment: PaymentType,
    val items: List<OrderItemResponse> = listOf(),
    val trackingCode: String,
    val assignedDelivery: StaffResponse? = null,
)
