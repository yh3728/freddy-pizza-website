package com.freddypizza.website.request.admin

import com.freddypizza.website.enums.ProductCategory
import java.math.BigDecimal

data class AdminProductRequest(
    val name: String,
    val description: String,
    val weight: Int,
    val ingredients: String,
    val price: BigDecimal,
    val quantity: Int = 0,
    val category: ProductCategory,
)
