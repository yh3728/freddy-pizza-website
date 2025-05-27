package com.freddypizza.website.response.user

import java.math.BigDecimal

data class CardItemResponse(
    val name: String,
    val description: String? = null,
    val ingredients: String? = null,
    val price: BigDecimal,
    val imagePath: String? = "/uploads/products/image_placeholder.png",
)
