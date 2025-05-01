package com.freddypizza.website.request.admin

import com.freddypizza.website.enums.OrderStatus

data class AdminOrderStatusRequest(
    val status: OrderStatus,
)
