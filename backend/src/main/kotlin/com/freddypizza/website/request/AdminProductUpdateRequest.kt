package com.freddypizza.website.request

import com.freddypizza.website.enums.ProductCategory
import java.math.BigDecimal

data class AdminProductUpdateRequest(
    val name: String? = null,
    val description: String? = null,
    val price: BigDecimal? = null,
    val isAvailable: Boolean? = null,
    val category: ProductCategory? = null,
)
