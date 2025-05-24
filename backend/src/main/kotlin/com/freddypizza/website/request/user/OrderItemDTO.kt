package com.freddypizza.website.request.user

data class OrderItemDTO (
    val productId: Long,
    val quantity: Int,
)