package com.freddypizza.website.response.user

import java.math.BigDecimal

data class MenuItemResponse(
    val name: String,
    val ingredients: String? = null,
    val weight: Int? = null,
    val price: BigDecimal,
    val imagePath: String? = "/uploads/products/image_placeholder.png",
)
