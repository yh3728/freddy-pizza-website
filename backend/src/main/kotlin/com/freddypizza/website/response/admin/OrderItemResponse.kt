package com.freddypizza.website.response.admin

import java.math.BigDecimal

data class OrderItemResponse(
    val productName: String,
    val quantity: Int,
    val price: BigDecimal,
)
