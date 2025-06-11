package com.freddypizza.website.response.admin

import com.freddypizza.website.enums.OrderStatus
import com.freddypizza.website.enums.PaymentType
import java.math.BigDecimal
import java.time.LocalDateTime

data class DeliveryOrderResponse(
    val id: Long,
    val status: OrderStatus,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val comment: String? = null,
    val address: String,
    val phone: String,
    val customerName: String,
    val payment: PaymentType,
    val trackingCode: String,
    val items: List<OrderItemResponse> = mutableListOf(),
    val assignedDelivery: StaffResponse? = null,
    val totalPrice: BigDecimal,
) : BaseOrderResponse()
