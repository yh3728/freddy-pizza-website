package com.freddypizza.website.request.user

import com.freddypizza.website.enums.PaymentType

data class CreateOrderRequest(
    val customerName: String,
    val phone: String,
    val address: String,
    val comment: String? = null,
    val items: List<OrderItemDTO>,
    val payment: PaymentType
)