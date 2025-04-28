package com.freddypizza.website.request

import com.freddypizza.website.enums.ProductCategory
import java.math.BigDecimal

data class AdminProductRequest(
    val name: String,
    val description: String? = null,
    val price: BigDecimal,
    val isAvailable: Boolean = false,
    val category: ProductCategory,
)
