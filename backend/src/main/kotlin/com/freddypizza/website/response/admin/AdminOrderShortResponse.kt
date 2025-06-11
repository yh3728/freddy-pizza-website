package com.freddypizza.website.response.admin

import com.freddypizza.website.enums.OrderStatus
import java.time.LocalDateTime

data class AdminOrderShortResponse(
    val id: Long,
    val status: OrderStatus,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val items: List<OrderItemResponse> = listOf(),
    val customerName: String,
    val comment: String? = null,
    val trackingCode: String,
    val assignedDelivery: StaffResponse? = null,
) : BaseOrderResponse()
