package com.freddypizza.website.response.admin

import com.freddypizza.website.enums.OrderStatus
import java.time.LocalDateTime

data class CookOrderShortResponse(
    val id: Long,
    val status: OrderStatus,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val items: List<OrderItemResponse> = listOf(),
    val comment: String? = null,
) : BaseOrderResponse()
