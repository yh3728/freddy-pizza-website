package com.freddypizza.website.response.admin

import com.freddypizza.website.enums.ProductCategory
import java.math.BigDecimal

data class AdminProductResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val weight: Int?,
    val ingredients: String?,
    val price: BigDecimal,
    val quantity: Int,
    val category: ProductCategory,
    val imagePath: String?,
)
