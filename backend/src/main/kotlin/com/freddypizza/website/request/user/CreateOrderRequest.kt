package com.freddypizza.website.request.user

data class CreateOrderRequest(
    val customerName: String,
    val phone: String,
    val address: String,
    val comment: String? = null,
    val items: List<OrderItemDTO>
)