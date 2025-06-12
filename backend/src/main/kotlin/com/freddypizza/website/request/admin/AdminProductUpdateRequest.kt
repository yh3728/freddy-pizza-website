package com.freddypizza.website.request.admin

import com.freddypizza.website.enums.ProductCategory
import java.math.BigDecimal

data class AdminProductUpdateRequest(
    val name: String? = null,
    val description: String? = null,
    val weight: Int? = null,
    val ingredients: String? = null,
    val price: BigDecimal? = null,
    val quantity: Int? = null,
    val category: ProductCategory? = null,
)
